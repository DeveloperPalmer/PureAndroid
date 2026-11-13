package ru.sla.pure.android.app.routing.predictive

internal fun shouldActivateBack(elapsedMillis: Long, progress: Float): Boolean {
  return elapsedMillis >= 100L && progress > 0.01f
}

internal fun normalizedBackProgress(progress: Float, activationProgress: Float): Float {
  val span = 1f - activationProgress
  return if (span > 0f) ((progress - activationProgress) / span).coerceIn(0f, 1f) else 1f
}

internal fun outgoingBackTransform(
  widthPx: Float,
  gapPx: Float,
  deviceCornerPx: Float,
  targetCornerPx: Float,
  scale: Float,
  back: Float,
): BackLayerTransform = BackLayerTransform(
  scale = interpolate(1f, MIN_SCALE, scale),
  translationX = (widthPx / 20f - gapPx).coerceAtLeast(0f) * scale,
  alpha = 1f - back,
  cornerRadiusPx = interpolate(deviceCornerPx, targetCornerPx, scale),
)

internal fun incomingBackTransform(
  widthPx: Float,
  deviceCornerPx: Float,
  targetCornerPx: Float,
  scale: Float,
  back: Float,
): BackLayerTransform = BackLayerTransform(
  scale = interpolate(interpolate(1f, MIN_SCALE, scale), 1f, back),
  translationX = -widthPx * REVEALED_SHIFT * (1f - back),
  alpha = 1f,
  cornerRadiusPx = interpolate(targetCornerPx, deviceCornerPx, back),
)

internal fun backScrimAlpha(backProgress: Float): Float {
  return SCRIM_ALPHA * (1f - backProgress)
}

private fun interpolate(start: Float, end: Float, fraction: Float): Float {
  return start * (1f - fraction) + end * fraction
}

private const val MIN_SCALE = 0.9f
private const val REVEALED_SHIFT = 0.15f
private const val SCRIM_ALPHA = 0.8f
