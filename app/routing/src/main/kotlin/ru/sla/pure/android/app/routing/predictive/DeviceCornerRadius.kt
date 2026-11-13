package ru.sla.pure.android.app.routing.predictive

import android.os.Build
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

@Composable
internal fun deviceCornerRadius(): Float {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return 0f
  val insets = LocalView.current.rootWindowInsets
  return listOf(
    RoundedCorner.POSITION_TOP_LEFT,
    RoundedCorner.POSITION_TOP_RIGHT,
    RoundedCorner.POSITION_BOTTOM_LEFT,
    RoundedCorner.POSITION_BOTTOM_RIGHT,
  ).mapNotNull { insets?.getRoundedCorner(it)?.radius }.maxOrNull()?.toFloat() ?: 0f
}
