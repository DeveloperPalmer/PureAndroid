package ru.sla.pure.android.app.routing

import ru.sla.pure.android.core.routing.NavigationTab

internal class TabNavigationPolicy(
  private val indices: Map<Any, Int>,
  val isTabBack: Boolean,
) {
  fun direction(fromEntry: Any, toEntry: Any): Int? = tabDirection(indices[fromEntry], indices[toEntry])
}

internal fun tabNavigationPolicy(
  state: NavigationState,
  tabs: List<NavigationTab>,
  entryProvider: EntryProvider,
): TabNavigationPolicy = TabNavigationPolicy(
  indices = tabs.flatMapIndexed { index, tab ->
    state.topLevelBackStacks.getValue(tab.route).map { route -> entryProvider(route).contentKey to index }
  }.toMap(),
  isTabBack = tabs.isNotEmpty() &&
    state.topLevelRoute != state.startRoute &&
    state.topLevelBackStacks.getValue(state.topLevelRoute).size == 1,
)

internal fun tabDirection(fromIndex: Int?, toIndex: Int?): Int? = when {
  fromIndex == null || toIndex == null || fromIndex == toIndex -> null
  toIndex > fromIndex -> 1
  else -> -1
}
