package ru.sla.pure.android.app.routing

import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.Navigator

/** Управляет одним контейнером. Вызывается из главного потока. */
@SingleIn(ForegroundScope::class)
class AppNavigator @Inject constructor() : Navigator {
  internal lateinit var initialRoute: NavKey
    private set

  private var navigationState: NavigationState? = null
  private var rootReplacement: NavKey? = null

  val currentRoute: NavKey?
    get() = navigationState?.let { state -> state.topLevelBackStacks.getValue(state.topLevelRoute).lastOrNull() }

  override fun setRoot(route: NavKey) {
    check(!::initialRoute.isInitialized || initialRoute == route) { "Flow root is already set" }
    initialRoute = route
  }

  fun attach(state: NavigationState) {
    navigationState = state
    val replacement = rootReplacement ?: return
    val backStack = state.topLevelBackStacks.values.single()
    // При пересоздании Activity снимок стека может быть сделан до получения события запуска.
    if (backStack.firstOrNull() == state.startRoute) {
      replaceRoot(replacement)
    }
  }

  fun detach(state: NavigationState) {
    if (navigationState === state) {
      navigationState = null
    }
  }

  override fun replaceRoot(route: NavKey) {
    val state = navigationState
    val backStack = state?.topLevelBackStacks?.values?.single()
    rootReplacement = route
    if (backStack == null) return
    if (backStack.singleOrNull() == route) return
    Snapshot.withMutableSnapshot {
      backStack.clear()
      backStack.add(route)
    }
  }

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

  override fun navigateBack() {
    val state = navigationState ?: return
    val backStack = state.topLevelBackStacks.getValue(state.topLevelRoute)
    if (backStack.size > 1) {
      backStack.removeLastOrNull()
    } else if (state.topLevelRoute != state.startRoute) {
      state.topLevelRoute = state.startRoute
    }
  }
}
