package ru.sla.pure.android.navigation

import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.Navigator

/** Переходы между экранами. Меняет [NavigationState], а `NavDisplay` показывает результат */
class AppNavigator(private val state: NavigationState) : Navigator {

    /** Ключ вкладки переключает вкладку, любой другой ключ кладётся в стек текущей */
    override fun navigateTo(route: NavKey) {
        if (route in state.backStacks) {
            state.topLevelRoute = route
        } else {
            state.backStacks.getValue(state.topLevelRoute).add(route)
        }
    }

    /** С корня вкладки — на стартовую вкладку, иначе — шаг назад в стеке текущей */
    override fun navigateBack() {
        val backStack = state.backStacks.getValue(state.topLevelRoute)
        if (backStack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            backStack.removeLastOrNull()
        }
    }
}
