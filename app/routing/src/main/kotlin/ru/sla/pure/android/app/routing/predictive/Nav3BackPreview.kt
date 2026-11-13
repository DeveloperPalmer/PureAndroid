package ru.sla.pure.android.app.routing.predictive

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.scene.NavigationBackHandler
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.SceneState
import androidx.navigation3.scene.rememberNavigationEventState
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventTransitionState.InProgress
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.NavigationEventState
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import kotlinx.coroutines.flow.first

/**
 * Совместимость с seek/settle Navigation 3 1.2 (NavDisplay.kt:743–787).
 * Preview удерживается на 0.5, пока слои следуют собственному прогрессу. Presence оставляет
 * обе сцены в композиции. После cancel пересоздаётся только display, иначе pop может унаследовать
 * no-op transform. SceneState с movable content должен оставаться снаружи key(displayGeneration).
 *
 * Удалять эти обходы можно после проверки повторного жеста, Back после cancel и interrupted push
 * из PredictiveBackTransitionTest на новой версии Navigation 3.
 */
internal class Nav3BackPreview(
  private val currentBackStack: () -> BackStackSnapshot,
) : BackPreviewPort {
  var displayGeneration by mutableIntStateOf(0)
    private set

  private var appliedGeneration by mutableIntStateOf(0)
  private var currentContext: BackContext? by mutableStateOf(null)
  private val registrations = mutableStateMapOf<Any, SceneRegistration>()
  private val input = DirectNavigationEventInput()
  private var session: PreviewSession? = null
  private var latestSessionId = 0L
  private var nextRegistrationId = 0L
  private var committedSessionId: Long? = null

  @Composable
  fun <T : Any> bind(
    sceneState: SceneState<T>,
    context: BackContext,
    controller: BackSessionController,
    onBack: () -> Unit,
  ): NavigationEventState<SceneInfo<T>> {
    val gestureState = rememberNavigationEventState(sceneState)
    val displayState = rememberNavigationEventState(sceneState)
    val displayOwner = rememberNavigationEventDispatcherOwner(parent = null)
    SideEffect { currentContext = context }
    DisposableEffect(displayOwner, input) {
      displayOwner.navigationEventDispatcher.addInput(input)
      onDispose { displayOwner.navigationEventDispatcher.removeInput(input) }
    }
    // Изолированный dispatcher получает только synthetic handler, а не вложенные флоу.
    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides displayOwner) {
      NavigationBackHandler(sceneState, displayState, onBackCompleted = onBack)
    }
    NavigationBackHandler(
      sceneState = sceneState,
      state = gestureState,
      onBackCompleted = { controller.onCompleted(context) },
      onBackCancelled = controller::onCancelled,
    )
    val gesture = gestureState.transitionState
    LaunchedEffect(gesture, context) {
      controller.onContextChanged(context)
      if (gesture is InProgress) controller.onProgress(context, gesture.latestEvent.progress)
    }
    return displayState
  }

  @Composable
  fun ScenePresence(key: Any, onOutgoingRemoved: (Long) -> Unit) {
    val transition = LocalNavAnimatedContentScope.current.transition
    DisposableEffect(this, key, transition) {
      val registration = SceneRegistration(++nextRegistrationId, transition)
      registrations[key] = registration
      onDispose {
        if (registrations[key]?.id == registration.id) registrations.remove(key)
        val active = session
        if (active != null && active.outgoingRegistrationId == registration.id && committedSessionId == active.id) {
          session = null
          onOutgoingRemoved(active.id)
        }
      }
    }
    transition.animateFloat(transitionSpec = { tween(PRESENCE_MS) }, label = "predictiveScenePresence") {
      if (it == EnterExitState.Visible) 1f else 0f
    }
  }

  fun onDisplayApplied(generation: Int) {
    appliedGeneration = generation
  }

  override suspend fun start(sessionId: Long, context: BackContext): PreviewResult {
    if (sessionId <= latestSessionId || !isCurrent(context)) {
      return PreviewResult.Obsolete
    }
    latestSessionId = sessionId
    val active = PreviewSession(sessionId)
    session = active
    snapshotFlow {
      !isCurrent(context) || registrations[context.outgoingKey]?.transition?.let {
        // После cancel currentState может остаться PostExit: ждём лишь окончания первоначального входа.
        it.currentState != EnterExitState.PreEnter && it.targetState == EnterExitState.Visible
      } == true
    }.first { it }
    if (session?.id != active.id || !isCurrent(context)) {
      return PreviewResult.Obsolete
    }
    session = active.copy(started = true)
    input.backStarted(NavigationEvent(progress = 0.5f))
    return PreviewResult.Ready
  }

  override fun complete(sessionId: Long, context: BackContext): CommitResult {
    if (committedSessionId == sessionId) return CommitResult.AlreadyApplied
    if (sessionId < latestSessionId ||
      (sessionId == latestSessionId && session?.id != sessionId) || !isCurrent(context)
    )
      return CommitResult.Obsolete

    val active = session?.takeIf { it.id == sessionId } ?: PreviewSession(sessionId)
    latestSessionId = sessionId
    committedSessionId = sessionId
    session = active.copy(
      outgoingRegistrationId = if (active.started) registrations[context.outgoingKey]?.id else null,
    )
    // Проверка живых стеков и pop выполняются на Main без suspend между ними.
    // Token помечен использованным до callback, чтобы повторное завершение не вызвало второй pop.
    input.backCompleted()
    if (!active.started) session = null
    return CommitResult.Applied
  }

  override suspend fun cancel(sessionId: Long) {
    val active = session?.takeIf { it.id == sessionId } ?: return
    session = null
    if (active.started && committedSessionId != sessionId) {
      input.backCancelled()
      val generation = ++displayGeneration
      snapshotFlow { appliedGeneration >= generation }.first { it }
    }
  }

  private fun isCurrent(context: BackContext): Boolean =
    currentContext == context && currentBackStack() == context.stack

  private data class PreviewSession(
    val id: Long,
    val started: Boolean = false,
    val outgoingRegistrationId: Long? = null,
  )

  // ID различает повторные регистрации одного Transition и защищает от позднего onDispose.
  private data class SceneRegistration(
    val id: Long,
    val transition: Transition<EnterExitState>
  )

  private companion object {
    const val PRESENCE_MS = 250
  }
}
