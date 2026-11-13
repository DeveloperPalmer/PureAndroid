package ru.sla.pure.android.app.routing.predictive

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.rememberSceneState
import androidx.navigation3.ui.NavDisplay
import ru.sla.pure.android.app.routing.NavigationBarSceneDecoratorStrategy
import ru.sla.pure.android.app.routing.SlideTransitions
import ru.sla.pure.android.app.routing.SlideTransitions.betweenTabs
import ru.sla.pure.android.app.routing.TabNavigationPolicy

/** Собирает владельцев состояния; решения о жизненном цикле Back принимает controller. */
@Composable
internal fun <T : Any> PredictiveNavDisplay(
  entries: List<NavEntry<T>>,
  sharedTransitionScope: SharedTransitionScope,
  navigationBarDecorator: NavigationBarSceneDecoratorStrategy<T>,
  tabPolicy: TabNavigationPolicy,
  currentBackStack: () -> BackStackSnapshot,
  gestureUptimeMillis: () -> Long,
  onBack: () -> Unit,
) {
  val latestBackStack by rememberUpdatedState(currentBackStack)
  val latestUptimeMillis by rememberUpdatedState(gestureUptimeMillis)
  val preview = remember { Nav3BackPreview { latestBackStack() } }
  val scope = rememberCoroutineScope()
  val controller = remember(preview, scope) {
    BackSessionController(scope, preview) { latestUptimeMillis() }
  }
  DisposableEffect(controller) {
    onDispose { controller.dispose() }
  }
  val predictiveDecorator = remember(controller, preview) { PredictiveBackSceneDecorator<T>(controller, preview) }
  val sceneState = rememberSceneState(
    entries = entries,
    sceneStrategies = emptyList(),
    sceneDecoratorStrategies = listOf(navigationBarDecorator, predictiveDecorator),
    sharedTransitionScope = sharedTransitionScope,
    onBack = onBack,
  )
  val context = BackContext(
    stack = currentBackStack(),
    outgoingKey = sceneState.currentScene.key,
    incomingKey = sceneState.previousScenes.lastOrNull()?.key,
    isTabBack = tabPolicy.isTabBack,
  )
  val displayState = preview.bind(sceneState, context, controller, onBack)
  Box(
    Modifier
      .fillMaxSize()
      .drawBehind {
        if (controller.previewVisible) drawRect(Color.Black, alpha = backScrimAlpha(controller.backProgress))
      },
  ) {
    val generation = preview.displayGeneration
    key(generation) {
      NavDisplay(
        sceneState = sceneState,
        navigationEventState = displayState,
        transitionSpec = { betweenTabs(tabPolicy) ?: SlideTransitions.forward<T>().invoke(this) },
        popTransitionSpec = {
          betweenTabs(tabPolicy) ?: if (controller.suppressPopTransition) {
            EnterTransition.None togetherWith ExitTransition.KeepUntilTransitionsFinished
          } else {
            SlideTransitions.back<T>().invoke(this)
          }
        },
        predictivePopTransitionSpec = {
          EnterTransition.None togetherWith ExitTransition.KeepUntilTransitionsFinished
        },
      )
      SideEffect { preview.onDisplayApplied(generation) }
    }
  }
}
