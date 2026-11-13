package ru.sla.pure.android.app.routing.predictive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope

internal class PredictiveBackSceneDecorator<T : Any>(
  private val controller: BackSessionController,
  private val preview: Nav3BackPreview,
) : SceneDecoratorStrategy<T> {
  override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> =
    PredictiveBackScene(scene, controller, preview)
}

private data class PredictiveBackScene<T : Any>(
  val scene: Scene<T>,
  val controller: BackSessionController,
  val preview: Nav3BackPreview,
) : Scene<T> by scene {
  override val key: Any = scene::class to scene.key

  override val content: @Composable () -> Unit = {
    preview.ScenePresence(key, controller::onOutgoingRemoved)
    val deviceCorner = deviceCornerRadius()
    Box(
      Modifier
        .fillMaxSize()
        .graphicsLayer {
          if (controller.previewVisible) {
            val transform = when (key) {
              controller.outgoingKey -> outgoingBackTransform(
                widthPx = size.width,
                gapPx = EDGE_GAP.dp.toPx(),
                deviceCornerPx = deviceCorner,
                targetCornerPx = CORNER.dp.toPx(),
                scale = controller.scaleProgress,
                back = controller.backProgress,
              )
              controller.incomingKey -> incomingBackTransform(
                widthPx = size.width,
                deviceCornerPx = deviceCorner,
                targetCornerPx = CORNER.dp.toPx(),
                scale = controller.scaleProgress,
                back = controller.backProgress,
              )
              else -> null
            }
            if (transform != null) applyTransform(transform)
          }
        }
        .drawWithContent {
          drawContent()
          if (controller.previewVisible && key == controller.incomingKey) {
            drawRect(Color.Black, alpha = backScrimAlpha(controller.backProgress))
          }
        },
    ) {
      scene.content()
    }
  }
}

private fun GraphicsLayerScope.applyTransform(transform: BackLayerTransform) {
  scaleX = transform.scale
  scaleY = transform.scale
  translationX = transform.translationX
  translationY = 0f
  alpha = transform.alpha
  shape = RoundedCornerShape(transform.cornerRadiusPx)
  clip = true
}

private const val EDGE_GAP = 8
private const val CORNER = 32
