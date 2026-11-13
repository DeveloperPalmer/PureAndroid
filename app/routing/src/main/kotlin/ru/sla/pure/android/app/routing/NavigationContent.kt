package ru.sla.pure.android.app.routing

import android.os.SystemClock
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.app.routing.predictive.BackStackSnapshot
import ru.sla.pure.android.app.routing.predictive.PredictiveNavDisplay
import ru.sla.pure.android.core.routing.NavigationTab

@Composable
internal fun NavigationContent(
  state: NavigationState,
  navigator: AppNavigator,
  entryProvider: EntryProvider,
  modifier: Modifier = Modifier,
  tabs: List<NavigationTab> = emptyList(),
  gestureUptimeMillis: () -> Long = SystemClock::uptimeMillis,
) {
  DisposableEffect(navigator, state) {
    navigator.attach(state)
    onDispose { navigator.detach(state) }
  }
  SharedTransitionLayout(modifier) {
    val navigationBarDecorator = rememberNavigationBarSceneDecoratorStrategy<NavKey>(
      sharedTransitionScope = this,
      navigationBar = {
        if (tabs.isNotEmpty()) AppNavigationBar(tabs, state.topLevelRoute, navigator::navigateTo)
      },
    )
    PredictiveNavDisplay(
      entries = state.entries(entryProvider),
      onBack = navigator::navigateBack,
      navigationBarDecorator = navigationBarDecorator,
      sharedTransitionScope = this,
      tabPolicy = tabNavigationPolicy(state, tabs, entryProvider),
      currentBackStack = {
        // Читаем сами стеки при commit, даже если композиция ещё не увидела navigateTo/смену вкладки.
        BackStackSnapshot(
          activeTab = state.topLevelRoute,
          entries = state.stacksInUse.flatMap { tab ->
            state.topLevelBackStacks.getValue(tab).map { entryProvider(it).contentKey }
          },
        )
      },
      gestureUptimeMillis = gestureUptimeMillis,
    )
  }
}
