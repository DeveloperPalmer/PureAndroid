package ru.sla.pure.android.feature.app

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.Navigator

/** Переходы между экранами. Меняет [NavigationState], а `NavDisplay` показывает результат */
@SingleIn(AppFlowScope::class)
class AppNavigator @Inject constructor() : Navigator {
  private var navigationState: NavigationState? = null

  fun attach(state: NavigationState) {
    navigationState = state
  }

  fun detach(state: NavigationState) {
    if (navigationState === state) {
      navigationState = null
    }
  }

  /** Ключ вкладки переключает вкладку, любой другой ключ кладётся в стек текущей */
  override fun navigateTo(route: NavKey) {
    val state = navigationState ?: return
    if (route in state.topLevelBackStacks) {
      state.topLevelRoute = route
    } else {
      val backStack = state.topLevelBackStacks.getValue(state.topLevelRoute)
      if (backStack.last() != route) {
        backStack.add(route)
      }
    }
  }

  /** С корня вкладки — на стартовую вкладку, иначе — шаг назад в стеке текущей */
  override fun navigateBack() {
    val state = navigationState ?: return
    val backStack = state.topLevelBackStacks.getValue(state.topLevelRoute)
    if (backStack.last() == state.topLevelRoute) {
      state.topLevelRoute = state.startRoute
    } else {
      backStack.removeLastOrNull()
    }
  }
}
