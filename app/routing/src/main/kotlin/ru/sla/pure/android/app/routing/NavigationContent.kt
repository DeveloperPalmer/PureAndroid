package ru.sla.pure.android.app.routing

import android.os.SystemClock
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.app.routing.predictive.BackStackSnapshot
import ru.sla.pure.android.app.routing.predictive.PredictiveNavDisplay
import ru.sla.pure.android.core.routing.NavigationTab

@Composable
internal fun NavigationContent(
  state: NavigationState,
  entryProvider: EntryProvider,
  modifier: Modifier = Modifier,
  tabs: List<NavigationTab> = emptyList(),
  gestureUptimeMillis: () -> Long = SystemClock::uptimeMillis,
) {
  SharedTransitionLayout(modifier) {
    val entries = state.entries(entryProvider)
    if (entries.isEmpty()) {
      return@SharedTransitionLayout
    }
    val navigationBarDecorator = rememberNavigationBarSceneDecoratorStrategy<NavKey>(
      sharedTransitionScope = this,
      navigationBar = { if (tabs.isNotEmpty()) AppNavigationBar(tabs, state.topLevelRoute, state::push) },
    )
    PredictiveNavDisplay(
      entries = entries,
      onBack = state::pop,
      navigationBarDecorator = navigationBarDecorator,
      sharedTransitionScope = this,
      tabPolicy = tabNavigationPolicy(state, tabs, entryProvider),
      currentBackStack = {
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
