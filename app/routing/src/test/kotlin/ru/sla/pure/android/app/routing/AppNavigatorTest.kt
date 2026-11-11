package ru.sla.pure.android.app.routing

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AppNavigatorTest {
  private enum class Route : NavKey { Initial, Main, Details, Map }

  @Test
  fun rootReplacementRemovesInitialResolveFromBackStack() {
    val navigator = AppNavigator()
    val stack = NavBackStack<NavKey>(Route.Initial)
    assertNull(navigator.currentRoute)
    navigator.attach(initialState(stack))
    assertEquals(Route.Initial, navigator.currentRoute)

    navigator.replaceRoot(Route.Main)
    navigator.replaceRoot(Route.Main)
    navigator.navigateBack()

    assertEquals(listOf(Route.Main), stack.toList())
    assertEquals(Route.Main, navigator.currentRoute)
  }

  @Test
  fun rootReplacementRequestedWhileDetachedAppliesOnAttach() {
    val navigator = AppNavigator()
    val previousStack = NavBackStack<NavKey>(Route.Initial)
    val previousState = initialState(previousStack)
    navigator.attach(previousState)
    navigator.detach(previousState)
    navigator.replaceRoot(Route.Main)

    val restoredStack = NavBackStack<NavKey>(Route.Initial)
    navigator.attach(initialState(restoredStack))
    navigator.detach(previousState)

    assertEquals(listOf(Route.Initial), previousStack.toList())
    assertEquals(listOf(Route.Main), restoredStack.toList())
  }

  @Test
  fun rootReplacementSurvivesReattachmentWithAnOlderSnapshot() {
    val navigator = AppNavigator()
    val originalState = initialState(NavBackStack<NavKey>(Route.Initial))
    navigator.attach(originalState)
    navigator.replaceRoot(Route.Main)
    navigator.detach(originalState)

    val restoredStack = NavBackStack<NavKey>(Route.Initial)
    navigator.attach(initialState(restoredStack))

    assertEquals(listOf(Route.Main), restoredStack.toList())
  }

  @Test
  fun restoredRootIsKeptWhenNavigatorAttaches() {
    val navigator = AppNavigator()
    val stack = NavBackStack<NavKey>(Route.Main)
    navigator.attach(initialState(stack))

    assertEquals(listOf(Route.Main), stack.toList())
    assertEquals(Route.Main, navigator.currentRoute)
  }

  @Test
  fun tabStacksSurviveSwitchingAndBackReturnsToMain() {
    val navigator = AppNavigator()
    val mainStack = NavBackStack<NavKey>(Route.Main)
    val mapStack = NavBackStack<NavKey>(Route.Map)
    val state = NavigationState(
      startRoute = Route.Main,
      topLevelRoute = mutableStateOf<NavKey>(Route.Main),
      topLevelBackStacks = linkedMapOf(Route.Main to mainStack, Route.Map to mapStack),
    )
    navigator.attach(state)

    navigator.navigateTo(Route.Details)
    navigator.navigateTo(Route.Map)
    navigator.navigateTo(Route.Map)
    assertEquals(Route.Map, state.topLevelRoute)
    assertEquals(listOf(Route.Map), mapStack.toList())

    navigator.navigateBack()
    assertEquals(Route.Main, state.topLevelRoute)
    assertEquals(listOf(Route.Main, Route.Details), mainStack.toList())

    navigator.navigateBack()
    assertEquals(listOf(Route.Main), mainStack.toList())
  }

  private fun initialState(stack: NavBackStack<NavKey>) = NavigationState(
    startRoute = Route.Initial,
    topLevelRoute = mutableStateOf<NavKey>(Route.Initial),
    topLevelBackStacks = mapOf(Route.Initial to stack),
  )
}
