package ru.sla.pure.android.app.routing

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.routing.deeplink.PendingDeepLink

class AppNavigatorTest {
  private enum class Route : NavKey { Initial, Main, Details, Map }

  private val appFlowKey = FlowNavKey("app")
  private val pendingDeepLink = mockk<PendingDeepLink> { every { backStack } returns null }
  private val hostState = NavigationHostState(appFlowKey, pendingDeepLink)

  @Test
  fun rootReplacementRemovesInitialResolveFromBackStack() {
    val navigator = AppNavigator(hostState)
    val stack = NavBackStack<NavKey>(Route.Initial)
    assertNull(hostState.currentRoute)
    hostState.attach(appFlowKey, initialState(stack))
    assertEquals(Route.Initial, hostState.currentRoute)

    navigator.setRoot(Route.Main)
    navigator.setRoot(Route.Main)
    navigator.pop()

    assertEquals(listOf(Route.Main), stack.toList())
    assertEquals(Route.Main, hostState.currentRoute)
  }

  @Test
  fun rootReplacementRequestedWhileDetachedAppliesOnAttach() {
    val navigator = AppNavigator(hostState)
    val previousStack = NavBackStack<NavKey>(Route.Initial)
    val previousState = initialState(previousStack)
    hostState.attach(appFlowKey, previousState)
    hostState.detach(appFlowKey, previousState)
    navigator.setRoot(Route.Main)

    val restoredStack = NavBackStack<NavKey>(Route.Initial)
    hostState.attach(appFlowKey, initialState(restoredStack))
    hostState.detach(appFlowKey, previousState)

    assertEquals(listOf(Route.Initial), previousStack.toList())
    assertEquals(listOf(Route.Main), restoredStack.toList())
  }

  @Test
  fun rootReplacementSurvivesReattachmentWithAnOlderSnapshot() {
    val navigator = AppNavigator(hostState)
    val originalState = initialState(NavBackStack<NavKey>(Route.Initial))
    hostState.attach(appFlowKey, originalState)
    navigator.setRoot(Route.Main)
    hostState.detach(appFlowKey, originalState)

    val restoredStack = NavBackStack<NavKey>(Route.Initial)
    hostState.attach(appFlowKey, initialState(restoredStack))

    assertEquals(listOf(Route.Main), restoredStack.toList())
  }

  @Test
  fun restoredRootIsKeptWhenHostAttaches() {
    val stack = NavBackStack<NavKey>(Route.Main)
    hostState.attach(appFlowKey, initialState(stack))

    assertEquals(listOf(Route.Main), stack.toList())
    assertEquals(Route.Main, hostState.currentRoute)
  }

  @Test
  fun tabStacksSurviveSwitchingAndBackReturnsToMain() {
    val navigator = AppNavigator(hostState)
    val mainFlowKey = FlowNavKey("main")
    val rootStack = NavBackStack<NavKey>(Route.Initial)
    hostState.attach(appFlowKey, initialState(rootStack))
    navigator.setRoot(mainFlowKey)
    val mainStack = NavBackStack<NavKey>(Route.Main)
    val mapStack = NavBackStack<NavKey>(Route.Map)
    val state = NavigationState(
      startRoute = Route.Main,
      topLevelRoute = mutableStateOf<NavKey>(Route.Main),
      topLevelBackStacks = linkedMapOf(Route.Main to mainStack, Route.Map to mapStack),
    )
    hostState.attach(mainFlowKey, state)

    navigator.push(Route.Details)
    navigator.push(Route.Map)
    navigator.push(Route.Map)
    assertEquals(Route.Map, state.topLevelRoute)
    assertEquals(listOf(Route.Map), mapStack.toList())

    navigator.pop()
    assertEquals(Route.Main, state.topLevelRoute)
    assertEquals(listOf(Route.Main, Route.Details), mainStack.toList())

    navigator.pop()
    assertEquals(listOf(Route.Main), mainStack.toList())

    navigator.push(Route.Details)
    navigator.setRoot(Route.Initial)
    navigator.push(Route.Map)
    assertEquals(listOf(Route.Initial, Route.Map), rootStack.toList())
    navigator.pop()
    assertEquals(listOf(Route.Initial), rootStack.toList())
    assertEquals(listOf(Route.Main, Route.Details), mainStack.toList())
  }

  private fun initialState(stack: NavBackStack<NavKey>) = NavigationState(
    startRoute = Route.Initial,
    topLevelRoute = mutableStateOf<NavKey>(Route.Initial),
    topLevelBackStacks = mapOf(Route.Initial to stack),
  )
}
