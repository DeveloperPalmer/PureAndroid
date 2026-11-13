package ru.sla.pure.android.app.routing.predictive

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Единственный владелец порядка prepare → drag → commit/cancel → отложенное действие. */
@Stable
internal class BackSessionController(
  private val scope: CoroutineScope,
  private val preview: BackPreviewPort,
  private val uptimeMillis: () -> Long,
) {
  var outgoingKey: Any? by mutableStateOf(null)
    private set
  var incomingKey: Any? by mutableStateOf(null)
    private set
  var previewVisible by mutableStateOf(false)
    private set
  var suppressPopTransition by mutableStateOf(false)
    private set
  var scaleProgress by mutableFloatStateOf(0f)
    private set
  var backProgress by mutableFloatStateOf(0f)
    private set

  private var phase = Phase.Idle
  private var nextSessionId = 0L
  private var session: Session? = null
  private var work: Job? = null
  private var pending: PendingAction? = null
  private var latestProgress = 0f
  private var activationProgress = 0f

  fun onProgress(context: BackContext, progress: Float) {
    if (phase == Phase.Cancelling || phase == Phase.AwaitingRemoval) {
      if (pending !is PendingAction.Complete) pending = PendingAction.Gesture(context, progress)
      return
    }
    if (phase == Phase.Completing) return
    val active = session ?: begin(context)
    if (active.context != context) {
      cancel(animateReturn = false, next = PendingAction.Gesture(context, progress))
      return
    }
    latestProgress = progress.coerceIn(0f, 1f)
    if (context.isTabBack || context.incomingKey == null) return
    if (phase == Phase.Pending && shouldActivateBack(uptimeMillis() - active.startedAt, latestProgress)) {
      phase = Phase.Preparing
      launchWork {
        val result = preview.start(active.id, context)
        if (session?.id != active.id || phase != Phase.Preparing) {
          return@launchWork
        }
        if (result == PreviewResult.Obsolete) {
          cancel(animateReturn = false)
          return@launchWork
        }
        activationProgress = latestProgress
        scaleProgress = 0f
        previewVisible = true
        phase = Phase.Dragging
      }
    } else if (phase == Phase.Dragging) {
      scaleProgress = DECELERATE.transform(normalizedBackProgress(latestProgress, activationProgress))
    }
  }

  fun onCompleted(context: BackContext) {
    if (phase == Phase.Cancelling) {
      pending = PendingAction.Complete(context)
      return
    }
    if (phase == Phase.Completing || phase == Phase.AwaitingRemoval) {
      pending = null
      return
    }
    val active = session ?: begin(context)
    if (active.context != context) {
      cancel(animateReturn = false)
      return
    }
    work?.cancel()
    if (!previewVisible) {
      // Быстрый жест, tab-back и кнопка не включают predictive-слои.
      if (preview.complete(active.id, context) == CommitResult.Obsolete) {
        cancel(animateReturn = false)
      } else {
        reset()
      }
      return
    }
    phase = Phase.Completing
    suppressPopTransition = true
    launchWork {
      coroutineScope {
        launch {
          animate(scaleProgress, 1f, animationSpec = tween(COMMIT_MS, easing = DECELERATE)) { value, _ ->
            scaleProgress = value
          }
        }
        animate(backProgress, 1f, animationSpec = tween(COMMIT_MS, easing = DECELERATE)) { value, _ ->
          backProgress = value
        }
      }
      if (session?.id != active.id) {
        return@launchWork
      }
      phase = Phase.AwaitingRemoval
      if (preview.complete(active.id, context) == CommitResult.Obsolete) cancel(animateReturn = false)
    }
  }

  fun onCancelled() {
    if (phase == Phase.Cancelling || phase == Phase.AwaitingRemoval) {
      if (pending is PendingAction.Gesture) pending = null
      return
    }
    if (phase == Phase.Idle || phase == Phase.Completing) return
    cancel(animateReturn = previewVisible)
  }

  fun onContextChanged(context: BackContext) {
    if (pending?.context != context) pending = null
    val active = session ?: return
    // После собственного pop исходящая сцена ещё удаляется; это ожидаемая смена context.
    if (phase != Phase.AwaitingRemoval && phase != Phase.Cancelling && active.context != context) {
      cancel(animateReturn = false)
    }
  }

  fun onOutgoingRemoved(sessionId: Long) {
    if (session?.id == sessionId && phase == Phase.AwaitingRemoval) {
      reset()
      resumePending()
    }
  }

  fun dispose() {
    work?.cancel()
    pending = null
    reset()
  }

  private fun begin(context: BackContext): Session {
    val active = Session(++nextSessionId, context, uptimeMillis())
    session = active
    outgoingKey = context.outgoingKey
    incomingKey = context.incomingKey
    phase = Phase.Pending
    return active
  }

  private fun cancel(animateReturn: Boolean, next: PendingAction? = null) {
    val active = session ?: return
    work?.cancel()
    pending = next
    phase = Phase.Cancelling
    suppressPopTransition = false
    launchWork {
      if (animateReturn) {
        animate(scaleProgress, 0f, animationSpec = tween(CANCEL_MS)) { value, _ -> scaleProgress = value }
      }
      preview.cancel(active.id)
      if (session?.id == active.id) {
        reset()
        resumePending()
      }
    }
  }

  private fun launchWork(block: suspend CoroutineScope.() -> Unit) {
    // Сначала сохраняем Job: на Main.immediate блок может завершиться без suspension
    // и запустить pending-операцию, чей Job нельзя затереть старым присваиванием.
    val job = scope.launch(start = CoroutineStart.LAZY, block = block)
    work = job
    job.start()
  }

  private fun reset() {
    session = null
    work = null
    phase = Phase.Idle
    outgoingKey = null
    incomingKey = null
    previewVisible = false
    suppressPopTransition = false
    scaleProgress = 0f
    backProgress = 0f
    latestProgress = 0f
    activationProgress = 0f
  }

  private fun resumePending() {
    val action = pending
    pending = null
    when (action) {
      is PendingAction.Complete -> onCompleted(action.context)
      is PendingAction.Gesture -> onProgress(action.context, action.progress)
      null -> Unit
    }
  }

  private data class Session(
    val id: Long,
    val context: BackContext,
    val startedAt: Long
  )

  private sealed interface PendingAction {
    val context: BackContext
    data class Complete(override val context: BackContext) : PendingAction
    data class Gesture(override val context: BackContext, val progress: Float) : PendingAction
  }

  private enum class Phase { Idle, Pending, Preparing, Dragging, Cancelling, Completing, AwaitingRemoval }

  private companion object {
    val DECELERATE = CubicBezierEasing(0f, 0f, 0f, 1f)
    const val COMMIT_MS = 250
    const val CANCEL_MS = 220
  }
}
