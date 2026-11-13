package ru.sla.pure.android.app.routing

import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.view.InputDevice
import android.view.MotionEvent
import android.view.WindowInsets
import androidx.activity.BackEventCompat
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowContainerKey
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.uikit.theme.AppTheme
import ru.sla.pure.android.uikit.window.setNavigationBarColorCompat
import java.io.File

@OptIn(ExperimentalTestApi::class)
class PredictiveBackTransitionTest : BaseTestCase() {
  @Test
  @DisplayName("Положение карточек одинаково для любой высоты жеста и событий кнопки Back")
  fun previewGeometryDoesNotDependOnTouchHeightOrInputSource() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val height = onRoot().fetchSemanticsNode().boundsInRoot.height
    val expected = sampleBack(scene, "center", touchY = height / 2f)
    finishBack(scene, cancelled = true)

    listOf(
      BackEventCompat.EDGE_LEFT to 0f,
      BackEventCompat.EDGE_RIGHT to height,
      BackEventCompat.EDGE_NONE to 0f,
      BackEventCompat.EDGE_NONE to height,
    ).forEach { (edge, touchY) ->
      val actual = sampleBack(scene, "edge-$edge-y-$touchY", swipeEdge = edge, touchY = touchY)
      expected.zip(actual).forEach { (reference, position) ->
        assertGeometryEquals(reference, position, "edge=$edge, touchY=$touchY")
      }
      finishBack(scene, cancelled = true)
    }
  }

  @Test
  @DisplayName("Удержание настоящей системной кнопки Back показывает тот же предпросмотр, что и жест")
  fun systemBackButtonHoldMatchesGesturePreview() = runScreenTest {
    if (Build.VERSION.SDK_INT < 36) {
      assumeTrue(false, "Predictive button back requires Android 16+")
      return@runScreenTest
    }
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val scene = showNavigation()
    push(scene, interrupted = false)
    val (buttonX, buttonY) = runOnIdle {
      val testActivity = checkNotNull(activity)
      val mode = Settings.Secure.getInt(testActivity.contentResolver, "navigation_mode", -1)
      assumeTrue(mode == 0, "Run in three-button navigation mode")
      val bounds = testActivity.windowManager.currentWindowMetrics.bounds
      assumeTrue(bounds.width() < bounds.height(), "Run in portrait with the Back button on the left")
      val barHeight = testActivity.window.decorView.rootWindowInsets
        .getInsets(WindowInsets.Type.navigationBars()).bottom
      // Системная панель не публикует кнопки в accessibility-дереве этого эмулятора.
      // Координаты стандартной левой кнопки вычисляем по размерам окна и системному inset.
      bounds.left + bounds.width() / 4f to bounds.bottom - barHeight / 2f
    }
    seekBack(1f)
    mainClock.advanceTimeBy(SETTLE_MS)
    val expected = runOnIdle { scene.position(1f) }
    finishBack(scene, cancelled = true)

    val downTime = SystemClock.uptimeMillis()
    fun inject(action: Int) {
      val event = MotionEvent.obtain(
        downTime,
        SystemClock.uptimeMillis(),
        action,
        buttonX,
        buttonY,
        0,
      )
      event.source = InputDevice.SOURCE_TOUCHSCREEN
      try {
        assertTrue(instrumentation.uiAutomation.injectInputEvent(event, true), "Inject system Back touch")
      } finally {
        event.recycle()
      }
    }

    var held = false
    try {
      inject(MotionEvent.ACTION_DOWN)
      held = true
      waitUntil(timeoutMillis = 5_000L) {
        mainClock.advanceTimeBy(32L)
        runOnIdle { scene.scale(Route.B) <= 0.901f }
      }
      runOnIdle {
        assertGeometryEquals(expected, scene.position(1f), "system button hold")
      }
      assertDestinationVisible(expected.b / 2f)
      val pixels = onRoot().captureToImage().toPixelMap()
      val side = pixels[pixels.width - 1, pixels.height / 2]
      val navigationBar = pixels[pixels.width - 1, pixels.height - 2]
      assertEquals(side.red, navigationBar.red, 0.01f, "No navigation bar scrim during hold: red")
      assertEquals(side.green, navigationBar.green, 0.01f, "No navigation bar scrim during hold: green")
      assertEquals(side.blue, navigationBar.blue, 0.01f, "No navigation bar scrim during hold: blue")
      inject(MotionEvent.ACTION_UP)
      held = false
      waitUntil(timeoutMillis = 5_000L) {
        mainClock.advanceTimeBy(32L)
        runOnIdle { scene.stack.toList() == listOf(Route.A) }
      }
      mainClock.advanceTimeBy(SETTLE_MS)
      onNodeWithTag(Route.B.name).assertDoesNotExist()
    } finally {
      if (held) inject(MotionEvent.ACTION_CANCEL)
    }
  }

  @Test
  @DisplayName("Системный Back во время возврата карточки выполняет слайд")
  fun systemBackSlidesDuringPredictiveCancellation() = backAfterCancellation(64L)

  @Test
  @DisplayName("Системный Back сразу после возврата карточки выполняет слайд")
  fun systemBackSlidesAtPredictiveCancellationBoundary() = backAfterCancellation(256L)

  @Test
  @DisplayName("Системный Back после сброса predictive-перехода выполняет слайд")
  fun systemBackSlidesAfterPredictiveTransitionReset() = backAfterCancellation(480L)

  @Test
  @DisplayName("После отмены predictive обычный системный Back снова выполняет слайд")
  fun systemBackSlidesAfterPredictiveCancellation() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val screenInstance = runOnIdle {
      scene.counters.getValue(Route.B).intValue = 42
      scene.instances.getValue(Route.B)
    }
    seekBack(0.5f)
    finishBack(scene, cancelled = true)
    runOnIdle {
      assertSame(screenInstance, scene.instances.getValue(Route.B), "Cancellation must preserve the composition")
      assertEquals(42, scene.counters.getValue(Route.B).intValue, "Cancellation must preserve screen state")
    }
    completeWithRegularSlide(scene)
  }

  @Test
  @DisplayName("После отмены predictive короткий жест Back снова выполняет слайд")
  fun quickGestureSlidesAfterPredictiveCancellation() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    finishBack(scene, cancelled = true)
    beginRawGesture()
    mainClock.advanceTimeBy(32L)
    rawProgress(0.8f)
    assertNoPreview(scene)
    completeWithRegularSlide(scene)
  }

  @Test
  @DisplayName("Быстрый жест до 100 мс не включает predictive и завершает Back обычным слайдом")
  fun quickGestureUsesRegularBackSlide() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    beginRawGesture()
    mainClock.advanceTimeBy(32L)
    rawProgress(0.8f)
    assertNoPreview(scene)
    completeWithRegularSlide(scene)
  }

  @Test
  @DisplayName("Отмена короткого жеста не анимируется и не блокирует следующую долгую протяжку")
  fun cancelledQuickGestureDoesNotBlockLongGesture() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    beginRawGesture()
    mainClock.advanceTimeBy(32L)
    rawProgress(0.7f)
    assertNoPreview(scene)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackCancelled() }
    mainClock.advanceTimeBy(SETTLE_MS)
    assertNoPreview(scene)
    seekBack(0.5f)
    runOnIdle { assertTrue(scene.scale(Route.B) in 0.9f..0.99f) }
    finishBack(scene, cancelled = false)
  }

  @Test
  @DisplayName("Долгая протяжка активируется после 100 мс без скачка от уже накопленного прогресса")
  fun longGestureStartsFromFullSizeAfterActivationDelay() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    beginRawGesture()
    mainClock.advanceTimeBy(64L)
    rawProgress(0.5f)
    assertNoPreview(scene)
    mainClock.advanceTimeBy(32L)
    rawProgress(0.6f)
    // Даём NavDisplay скомпоновать предыдущую сцену, не меняя положение пальца.
    mainClock.advanceTimeBy(64L)
    onNodeWithTag(Route.A.name).assertExists()
    runOnIdle { assertEquals(1f, scene.scale(Route.B), POSITION_TOLERANCE, "Activation must not jump") }
    rawProgress(0.75f)
    runOnIdle { assertTrue(scene.scale(Route.B) in 0.9f..0.99f) }
    finishBack(scene, cancelled = true)
  }

  @Test
  @DisplayName("Ожидание дольше 100 мс без заметной протяжки оставляет обычный Back")
  fun longGestureWithoutDragUsesRegularBackSlide() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    beginRawGesture()
    mainClock.advanceTimeBy(160L)
    rawProgress(0.005f)
    assertNoPreview(scene)
    completeWithRegularSlide(scene)
  }

  @Test
  @DisplayName("Завершённый predictive back после перехода возвращает A")
  fun completedBackAfterSettledPush() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    finishBack(scene, cancelled = false)
  }

  @Test
  @DisplayName("Отмена predictive back после перехода оставляет B")
  fun cancelledBackAfterSettledPush() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    finishBack(scene, cancelled = true)
  }

  @Test
  @DisplayName("После отмены новая predictive-протяжка снова анимируется и завершается")
  fun predictiveGestureWorksAgainAfterCancellation() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val first = sampleBack(scene, "before-cancel")
    finishBack(scene, cancelled = true)

    repeat(3) { repetition ->
      val repeated = sampleBack(scene, "after-cancel-$repetition")
      finishBack(scene, cancelled = repetition < 2)
      first.zip(repeated).forEach { (expected, actual) ->
        assertEquals(expected.b, actual.b, POSITION_TOLERANCE, "B position at progress=${expected.progress}")
        assertEquals(expected.scale, actual.scale, POSITION_TOLERANCE, "B scale at progress=${expected.progress}")
      }
    }
  }

  @Test
  @DisplayName("Новая протяжка подхватывается, даже если началась во время анимации отмены")
  fun predictiveGestureCanStartWhileCancellationIsAnimating() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    mainClock.advanceTimeBy(SETTLE_MS)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackCancelled() }
    mainClock.advanceTimeBy(64L)

    seekBack(0.5f)
    mainClock.advanceTimeBy(SETTLE_MS)
    onNodeWithTag(Route.A.name).assertExists()
    runOnIdle { assertTrue(scene.scale(Route.B) in 0.9f..0.99f, "The repeated gesture must animate B") }
    finishBack(scene, cancelled = false)
  }

  @Test
  @DisplayName("Завершённый predictive back во время перехода возвращает A")
  fun completedBackDuringPush() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = true)
    seekBack(0.5f)
    finishBack(scene, cancelled = false)
  }

  @Test
  @DisplayName("Отмена predictive back во время перехода оставляет B")
  fun cancelledBackDuringPush() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = true)
    seekBack(0.5f)
    finishBack(scene, cancelled = true)
  }

  @Test
  @DisplayName("Жест с любого края одинаково раскрывает предыдущий экран слева и корректно отменяется")
  fun bothEdgesRevealDestinationOnTheLeft() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val left = sampleBack(scene, "left-edge")
    finishBack(scene, cancelled = true)
    val right = sampleBack(scene, "right-edge", swipeEdge = BackEventCompat.EDGE_RIGHT)
    finishBack(scene, cancelled = true)
    left.zip(right).forEach { (expected, actual) ->
      assertEquals(expected.a, actual.a, POSITION_TOLERANCE, "A position at progress=${expected.progress}")
      assertEquals(expected.b, actual.b, POSITION_TOLERANCE, "B position at progress=${expected.progress}")
      assertEquals(expected.scale, actual.scale, POSITION_TOLERANCE, "B scale at progress=${expected.progress}")
    }
  }

  @Test
  @DisplayName("Подтверждённый жест удаляет экран из стека после финальной анимации")
  fun committedGestureFinishesAnimationBeforePopping() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    mainClock.advanceTimeBy(SETTLE_MS)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
    mainClock.advanceTimeBy(64L)
    runOnIdle { assertEquals(listOf(Route.A, Route.B), scene.stack.toList()) }
    mainClock.advanceTimeBy(SETTLE_MS)
    runOnIdle {
      assertEquals(listOf(Route.A), scene.stack.toList())
      assertEquals(0f, scene.x(Route.A), POSITION_TOLERANCE)
      assertEquals(1f, scene.scale(Route.A), POSITION_TOLERANCE)
    }
    onNodeWithTag(Route.B.name).assertDoesNotExist()
  }

  @Test
  @DisplayName("Затемнение вокруг карточки покрывает верхний и нижний системные отступы")
  fun predictiveBackdropCoversSystemInsetAreas() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val before = onRoot().captureToImage().toPixelMap()
    val topBefore = before[before.width - 1, 1]

    seekBack(0.5f)
    mainClock.advanceTimeBy(SETTLE_MS)
    val during = onRoot().captureToImage().toPixelMap()
    // Слева теперь виден предыдущий экран; фон вокруг карточки проверяем в правом зазоре.
    val backdropX = during.width - 1
    val side = during[backdropX, during.height / 2]
    val top = during[backdropX, 1]
    val bottom = during[backdropX, during.height - 2]
    capturePreview("full-window", 0.5f)
    assertTrue(top.luminance() < topBefore.luminance() * 0.5f, "The top inset must not keep its opaque background")
    listOf("top" to top, "bottom" to bottom).forEach { (area, color) ->
      assertEquals(side.red, color.red, 0.01f, "Red channel of the $area backdrop")
      assertEquals(side.green, color.green, 0.01f, "Green channel of the $area backdrop")
      assertEquals(side.blue, color.blue, 0.01f, "Blue channel of the $area backdrop")
    }
    finishBack(scene, cancelled = true)
  }

  @Test
  @DisplayName("Положение экранов при predictive back не зависит от завершения перехода вперёд")
  fun interruptedPushUsesSamePredictiveBackPositions() = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    val settled = sampleBack(scene, "settled")
    finishBack(scene, cancelled = false)

    push(scene, interrupted = true)
    val interrupted = sampleBack(scene, "interrupted")
    finishBack(scene, cancelled = false)

    assertAll(
      "Settled push: $settled; interrupted push: $interrupted",
      settled.zip(interrupted).flatMap { (expected, actual) ->
        listOf(
          Executable { assertEquals(expected.a, actual.a, POSITION_TOLERANCE, "A at progress=${expected.progress}") },
          Executable { assertEquals(expected.b, actual.b, POSITION_TOLERANCE, "B at progress=${expected.progress}") },
          Executable {
            assertEquals(expected.scale, actual.scale, POSITION_TOLERANCE, "B scale at progress=${expected.progress}")
          },
        )
      },
    )
  }

  private fun assertGeometryEquals(expected: Position, actual: Position, context: String) {
    assertEquals(expected.a, actual.a, POSITION_TOLERANCE, "Destination X: $context")
    assertEquals(expected.aY, actual.aY, POSITION_TOLERANCE, "Destination Y: $context")
    assertEquals(expected.b, actual.b, POSITION_TOLERANCE, "Card X: $context")
    assertEquals(expected.bY, actual.bY, POSITION_TOLERANCE, "Card Y: $context")
    assertEquals(expected.scale, actual.scale, POSITION_TOLERANCE, "Card scale: $context")
  }

  private fun backAfterCancellation(delayMs: Long) = runScreenTest {
    val scene = showNavigation()
    push(scene, interrupted = false)
    seekBack(0.5f)
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackCancelled() }
    mainClock.advanceTimeBy(delayMs)
    // Проверяем середину слайда, учитывая остаток возврата карточки при раннем Back.
    completeWithRegularSlide(scene, delayMs = if (delayMs < 224L) 352L - delayMs else 96L)
  }

  private fun AndroidComposeUiTest<ComponentActivity>.showNavigation(): TestScene {
    val scene = TestScene()
    val clock = { mainClock.currentTime }
    val installer = EntryProviderInstaller {
      entry(Route.Flow, metadata = metadata { put(FlowContainerKey, true) }) {
        NavigationContent(
          state = scene.state,
          navigator = scene.navigator,
          gestureUptimeMillis = clock,
          entryProvider = entryProvider<NavKey> {
            listOf(Route.A, Route.B).forEach { route ->
              entry(route, metadata = if (route == Route.A) showNavigationBar() else emptyMap()) {
                val instance = remember { Any() }
                val counter = rememberSaveable { mutableIntStateOf(0) }
                SideEffect {
                  scene.instances[route] = instance
                  scene.counters[route] = counter
                }
                Box(
                  Modifier
                    .fillMaxSize()
                    .background(if (route == Route.A) Color.Red else Color.Blue)
                    .testTag(route.name)
                    .onGloballyPositioned { coordinates ->
                      scene.coordinates[route] = coordinates
                    },
                )
              }
            }
          },
        )
      }
    }
    val rootNavigator = AppNavigator()
    runOnUiThread {
      checkNotNull(activity).apply {
        enableEdgeToEdge()
        window.setNavigationBarColorCompat(Color.Transparent.toArgb(), contrastEnforced = false)
      }
    }
    setContent {
      AppTheme {
        AppFlowHost(
          flowRoute = Route.Flow,
          navigator = rootNavigator,
          entryProviderInstallers = setOf(installer),
        )
      }
    }
    waitForIdle()
    mainClock.autoAdvance = false
    return scene
  }

  private fun AndroidComposeUiTest<ComponentActivity>.push(scene: TestScene, interrupted: Boolean) {
    runOnIdle { scene.navigator.navigateTo(Route.B) }
    // First compose B and start the spring; do not let waitForIdle finish it automatically.
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeBy(if (interrupted) INTERRUPTED_PUSH_MS else SETTLE_MS)
    runOnIdle {
      val x = scene.x(Route.B)
      if (interrupted) {
        assertTrue(x > POSITION_TOLERANCE && x < 1f, "Push must still be running: B.x=$x")
      } else {
        assertEquals(0f, x, POSITION_TOLERANCE, "Push must have finished")
      }
      assertEquals(listOf(Route.A, Route.B), scene.stack.toList())
    }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.seekBack(
    progress: Float,
    start: Boolean = true,
    swipeEdge: Int = BackEventCompat.EDGE_LEFT,
    touchY: Float = 0f,
  ) {
    if (start) {
      runOnIdle {
        checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackStarted(backEvent(0f, swipeEdge, touchY))
      }
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeBy(112L)
      runOnIdle {
        checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackProgressed(backEvent(0.02f, swipeEdge, touchY))
      }
      // Готовим осознанную протяжку и даём завершиться возможному входному переходу.
      mainClock.advanceTimeBy(SETTLE_MS)
      // Палец продолжает движение, в том числе после завершения отмены предыдущего жеста.
      runOnIdle {
        checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackProgressed(backEvent(0.03f, swipeEdge, touchY))
      }
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeByFrame()
    }
    runOnIdle {
      val dispatcher = checkNotNull(activity).onBackPressedDispatcher
      dispatcher.dispatchOnBackProgressed(backEvent(progress, swipeEdge, touchY))
    }
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeByFrame()
  }

  private fun AndroidComposeUiTest<ComponentActivity>.beginRawGesture() {
    runOnIdle {
      checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackStarted(backEvent(0f, BackEventCompat.EDGE_LEFT))
    }
    mainClock.advanceTimeByFrame()
  }

  private fun AndroidComposeUiTest<ComponentActivity>.rawProgress(progress: Float) {
    runOnIdle {
      checkNotNull(activity).onBackPressedDispatcher.dispatchOnBackProgressed(
        backEvent(progress, BackEventCompat.EDGE_LEFT),
      )
    }
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeByFrame()
  }

  private fun AndroidComposeUiTest<ComponentActivity>.assertNoPreview(scene: TestScene) {
    runOnIdle {
      assertEquals(listOf(Route.A, Route.B), scene.stack.toList())
      assertEquals(0f, scene.x(Route.B), POSITION_TOLERANCE)
      assertEquals(1f, scene.scale(Route.B), POSITION_TOLERANCE)
    }
    onNodeWithTag(Route.A.name).assertDoesNotExist()
  }

  private fun AndroidComposeUiTest<ComponentActivity>.completeWithRegularSlide(
    scene: TestScene,
    delayMs: Long = 96L,
  ) {
    runOnIdle { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeBy(delayMs)
    runOnIdle {
      assertEquals(listOf(Route.A), scene.stack.toList())
      assertTrue(scene.x(Route.A) < 0f, "A must slide in from the left")
      assertTrue(scene.x(Route.B) > 0f, "B must slide out to the right")
      assertEquals(1f, scene.scale(Route.B), POSITION_TOLERANCE, "A quick back must not shrink the screen")
    }
    mainClock.advanceTimeBy(SETTLE_MS)
    onNodeWithTag(Route.B.name).assertDoesNotExist()
    runOnIdle { assertEquals(0f, scene.x(Route.A), POSITION_TOLERANCE) }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.sampleBack(
    scene: TestScene,
    label: String,
    swipeEdge: Int = BackEventCompat.EDGE_LEFT,
    touchY: Float = 0f,
  ): List<Position> =
    listOf(0.25f, 0.5f, 0.75f).mapIndexed { index, progress ->
      seekBack(progress, start = index == 0, swipeEdge = swipeEdge, touchY = touchY)
      // Let any interrupted spring settle while the gesture stays at the same progress.
      mainClock.advanceTimeBy(SETTLE_MS)
      capturePreview(label, progress)
      runOnIdle {
        assertEquals(listOf(Route.A, Route.B), scene.stack.toList(), "Gesture must not pop early")
        scene.position(progress).also {
          assertTrue(it.scale in 0.9f..0.99f, "Predictive back must shrink B towards 90%: $it")
          val rightGap = 1f - it.b - it.scale
          assertTrue(it.b > rightGap, "Either gesture edge must leave a larger preview on the left: $it")
          assertTrue(it.a < 0f, "Either gesture edge must reveal A from the left: $it")
        }
      }.also {
        if (progress == 0.5f) {
          assertDestinationVisible(it.b / 2f)
        }
      }
    }

  private fun AndroidComposeUiTest<ComponentActivity>.assertDestinationVisible(xFraction: Float) {
    val pixels = onRoot().captureToImage().toPixelMap()
    val x = (pixels.width * xFraction).toInt().coerceIn(0, pixels.width - 1)
    val preview = pixels[x, pixels.height / 2]
    assertTrue(preview.red > preview.blue + 0.1f, "The larger gap must show the red destination screen")
  }

  private fun AndroidComposeUiTest<ComponentActivity>.capturePreview(label: String, progress: Float) {
    if (InstrumentationRegistry.getArguments().getString("predictiveBackScreenshots") != "true") return
    val directory = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)
    File(directory, "predictive-back-$label-$progress.png").outputStream().use {
      onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
    }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.finishBack(scene: TestScene, cancelled: Boolean) {
    runOnIdle {
      val dispatcher = checkNotNull(activity).onBackPressedDispatcher
      if (cancelled) dispatcher.dispatchOnBackCancelled() else dispatcher.onBackPressed()
    }
    mainClock.advanceTimeBy(SETTLE_MS)
    val visible = if (cancelled) Route.B else Route.A
    val removed = if (cancelled) Route.A else Route.B
    runOnIdle {
      assertEquals(if (cancelled) listOf(Route.A, Route.B) else listOf(Route.A), scene.stack.toList())
      assertEquals(0f, scene.x(visible), POSITION_TOLERANCE)
      assertEquals(1f, scene.scale(visible), POSITION_TOLERANCE)
    }
    onNodeWithTag(removed.name).assertDoesNotExist()
  }

  private fun backEvent(progress: Float, swipeEdge: Int, touchY: Float = 0f) =
    BackEventCompat(0f, touchY, progress, swipeEdge)

  @Serializable
  private enum class Route : NavKey { Flow, A, B }

  private class TestScene {
    val stack = NavBackStack<NavKey>(Route.A)
    val state = NavigationState(Route.A, mutableStateOf<NavKey>(Route.A), mapOf(Route.A to stack))
    val navigator = AppNavigator()
    val coordinates = mutableMapOf<Route, LayoutCoordinates>()
    val instances = mutableMapOf<Route, Any>()
    val counters = mutableMapOf<Route, MutableIntState>()

    fun position(progress: Float) = Position(
      progress = progress,
      a = x(Route.A),
      b = x(Route.B),
      scale = scale(Route.B),
      aY = y(Route.A),
      bY = y(Route.B),
    )

    fun y(route: Route): Float {
      val layout = coordinates.getValue(route)
      check(layout.isAttached) { "$route must still be rendered" }
      return layout.positionInRoot().y / layout.size.height
    }

    fun x(route: Route): Float {
      val layout = coordinates.getValue(route)
      check(layout.isAttached) { "$route must still be rendered" }
      return layout.positionInRoot().x / layout.size.width
    }

    fun scale(route: Route): Float {
      val layout = coordinates.getValue(route)
      check(layout.isAttached) { "$route must still be rendered" }
      val left = layout.localToRoot(Offset.Zero).x
      val right = layout.localToRoot(Offset(layout.size.width.toFloat(), 0f)).x
      return (right - left) / layout.size.width
    }
  }

  private data class Position(
    val progress: Float,
    val a: Float,
    val b: Float,
    val scale: Float,
    val aY: Float,
    val bY: Float,
  )

  private companion object {
    const val INTERRUPTED_PUSH_MS = 96L
    const val SETTLE_MS = 2_000L
    const val POSITION_TOLERANCE = 0.01f
  }
}
