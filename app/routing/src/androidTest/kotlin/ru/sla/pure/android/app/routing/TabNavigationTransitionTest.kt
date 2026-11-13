package ru.sla.pure.android.app.routing

import androidx.activity.BackEventCompat
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.routing.NavigationTab
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.uikit.theme.AppTheme

@OptIn(ExperimentalTestApi::class)
class TabNavigationTransitionTest : BaseTestCase() {
  @Test
  @DisplayName("Тапы по вкладкам справа и слева двигают страницы как HorizontalPager")
  fun tabTapsSlideInBothDirections() = runScreenTest {
    val scene = showTabs()
    tapTab(Route.Middle)
    assertSliding(scene, Route.First, Route.Middle, targetOnRight = true)
    settle(scene, Route.Middle)

    tapTab(Route.First)
    assertSliding(scene, Route.Middle, Route.First, targetOnRight = false)
    settle(scene, Route.First)
  }

  @Test
  @DisplayName("Переход между двумя не стартовыми вкладками учитывает их порядок")
  fun movingBetweenNonStartTabsUsesTheirOrder() = runScreenTest {
    val scene = showTabs()
    tapTab(Route.Last)
    settle(scene, Route.Last)
    tapTab(Route.Middle)
    assertSliding(scene, Route.Last, Route.Middle, targetOnRight = false)
    settle(scene, Route.Middle)
  }

  @Test
  @DisplayName("Back между вкладками не анимирует протяжку и отмену, после отпускания слайдит влево")
  fun tabBackStaysStillUntilCommitAndSlidesToLeftTab() = runScreenTest {
    val scene = showTabs()
    tapTab(Route.Last)
    settle(scene, Route.Last)
    listOf(0.25f, 0.5f, 0.75f).forEachIndexed { index, progress ->
      seekBack(progress, start = index == 0)
      assertStill(scene, Route.Last)
      onNodeWithTag(Route.First.name).assertDoesNotExist()
    }
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackCancelled() }
    mainClock.advanceTimeBy(SETTLE_MS)
    assertStill(scene, Route.Last)

    seekBack(0.5f)
    assertStill(scene, Route.Last)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
    assertSliding(scene, Route.Last, Route.First, targetOnRight = false)
    settle(scene, Route.First)
  }

  @Test
  @DisplayName("Back к стартовой вкладке справа запускает слайд вправо только после отпускания")
  fun tabBackSlidesToRightTabWhenStartTabIsOnRight() = runScreenTest {
    val scene = showTabs(start = Route.Middle)
    tapTab(Route.First)
    settle(scene, Route.First)
    seekBack(0.75f)
    assertStill(scene, Route.First)
    onNodeWithTag(Route.Middle.name).assertDoesNotExist()
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
    assertSliding(scene, Route.First, Route.Middle, targetOnRight = true)
    settle(scene, Route.Middle)
  }

  @Test
  @DisplayName("Back к вкладке с сохранёнными деталями использует pager-слайд и сохраняет её стек")
  fun tabBackRestoresSavedScreenWithTabSlide() = runScreenTest {
    val scene = showTabs(start = Route.Middle)
    runOnIdle { scene.navigator.navigateTo(Route.Details) }
    settle(scene, Route.Details)
    runOnIdle { scene.navigator.navigateTo(Route.First) }
    settle(scene, Route.First)
    seekBack(0.5f)
    assertStill(scene, Route.First)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
    assertSliding(scene, Route.First, Route.Details, targetOnRight = true)
    settle(scene, Route.Details)
    runOnIdle {
      assertEquals(listOf(Route.Middle, Route.Details), scene.stacks.getValue(Route.Middle).toList())
    }
  }

  @Test
  @DisplayName("Back с деталей внутри вкладки сохраняет predictive-масштабирование")
  fun backInsideTabStillUsesPredictiveAnimation() = runScreenTest {
    val scene = showTabs()
    tapTab(Route.Last)
    settle(scene, Route.Last)
    runOnIdle { scene.navigator.navigateTo(Route.Details) }
    settle(scene, Route.Details)
    seekBack(0.5f, predictive = true)
    runOnIdle {
      assertTrue(scene.scale(Route.Details) in 0.9f..0.99f, "Details must use predictive scaling")
      assertEquals(Route.Last, scene.state.topLevelRoute)
    }
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackCancelled() }
    settle(scene, Route.Details)
  }

  private fun AndroidComposeUiTest<ComponentActivity>.showTabs(start: Route = Route.First): TestScene {
    val scene = TestScene(start)
    val clock = { mainClock.currentTime }
    setContent {
      AppTheme {
        NavigationContent(
          state = scene.state,
          navigator = scene.navigator,
          tabs = tabs,
          gestureUptimeMillis = clock,
          entryProvider = entryProvider<NavKey> {
            Route.entries.forEach { route ->
              entry(route, metadata = if (route == Route.Details) emptyMap() else showNavigationBar()) {
                Box(
                  Modifier
                    .fillMaxSize()
                    .background(if (route == Route.First) Color.Red else Color.Blue)
                    .testTag(route.name)
                    .onGloballyPositioned { scene.coordinates[route] = it },
                )
              }
            }
          },
        )
      }
    }
    waitForIdle()
    mainClock.autoAdvance = false
    return scene
  }

  private fun AndroidComposeUiTest<ComponentActivity>.tapTab(route: Route) {
    val tab = tabs.single { it.route == route }
    onNodeWithText(checkNotNull(activity).getString(tab.label)).performClick()
  }

  private fun AndroidComposeUiTest<ComponentActivity>.seekBack(
    progress: Float,
    start: Boolean = true,
    predictive: Boolean = false,
  ) {
    if (predictive) {
      runOnIdle {
        checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackStarted(
          BackEventCompat(0f, 0f, 0f, BackEventCompat.EDGE_LEFT),
        )
      }
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeBy(112L)
      runOnIdle {
        checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackProgressed(
          BackEventCompat(0f, 0f, 0.02f, BackEventCompat.EDGE_LEFT),
        )
      }
      mainClock.advanceTimeBy(SETTLE_MS)
    }
    runOnIdle {
      val dispatcher = checkNotNull(activity).onBackPressedDispatcher
      if (start && !predictive) dispatcher.dispatchOnBackStarted(BackEventCompat(0f, 0f, 0f, BackEventCompat.EDGE_LEFT))
      dispatcher.dispatchOnBackProgressed(BackEventCompat(0f, 0f, progress, BackEventCompat.EDGE_LEFT))
    }
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeBy(SETTLE_MS)
  }

  private fun AndroidComposeUiTest<ComponentActivity>.assertSliding(
    scene: TestScene,
    from: Route,
    to: Route,
    targetOnRight: Boolean,
  ) {
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeBy(96L)
    runOnIdle {
      val direction = if (targetOnRight) 1f else -1f
      assertTrue(scene.x(to) * direction > 0.01f, "Incoming $to must slide from the correct side")
      assertTrue(scene.x(from) * direction < -0.01f, "Outgoing $from must slide towards the opposite side")
      assertEquals(1f, scene.scale(to), TOLERANCE)
      assertEquals(1f, scene.scale(from), TOLERANCE)
    }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.assertStill(scene: TestScene, route: Route) = runOnIdle {
    assertEquals(route, scene.navigator.currentRoute)
    assertEquals(0f, scene.x(route), TOLERANCE, "Tab must not move during predictive back")
    assertEquals(1f, scene.scale(route), TOLERANCE, "Tab must not shrink during predictive back")
  }

  private fun AndroidComposeUiTest<ComponentActivity>.settle(scene: TestScene, route: Route) {
    mainClock.advanceTimeBy(SETTLE_MS)
    assertStill(scene, route)
  }

  private enum class Route : NavKey { First, Middle, Last, Details }

  private class TestScene(start: Route) {
    val stacks: Map<NavKey, NavBackStack<NavKey>> = linkedMapOf(
      Route.First to NavBackStack<NavKey>(Route.First),
      Route.Middle to NavBackStack<NavKey>(Route.Middle),
      Route.Last to NavBackStack<NavKey>(Route.Last),
    )
    val state = NavigationState(start, mutableStateOf<NavKey>(start), stacks)
    val navigator = AppNavigator()
    val coordinates = mutableMapOf<Route, LayoutCoordinates>()

    fun x(route: Route): Float {
      val layout = coordinates.getValue(route)
      check(layout.isAttached) { "$route must still be rendered" }
      return layout.positionInRoot().x / layout.size.width
    }

    fun scale(route: Route): Float {
      val layout = coordinates.getValue(route)
      check(layout.isAttached) { "$route must still be rendered" }
      return (
        layout.localToRoot(Offset(layout.size.width.toFloat(), 0f)).x -
          layout.localToRoot(Offset.Zero).x
      ) / layout.size.width
    }
  }

  private companion object {
    const val SETTLE_MS = 2_000L
    const val TOLERANCE = 0.01f
    val tabs = listOf(
      NavigationTab(Route.First, R.drawable.ic_list_24, R.string.tab_main),
      NavigationTab(Route.Middle, R.drawable.ic_map_24, R.string.tab_map),
      NavigationTab(Route.Last, R.drawable.ic_list_24, R.string.profile_title),
    )
  }
}
