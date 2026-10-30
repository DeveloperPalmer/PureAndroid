package ru.sla.pure.android.uikit.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Длительности и кривые анимаций. Ряд длительностей короткий: переходы одного масштаба двигаются
 * одинаково, и интерфейс ощущается цельным
 */
@Immutable
class AppMotion {
  val smallMillis = 150
  val mediumMillis = 250
  val largeMillis = 400

  /** Основная кривая: быстрый старт и мягкое торможение — для переходов между состояниями */
  val emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

  /** Торможение без разгона — для появления и длинных перемещений */
  val decelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

  /** Мелкие изменения: цвет, прозрачность */
  fun <T> smallTween(): TweenSpec<T> = tween(durationMillis = smallMillis)

  /** Переходы между состояниями и экранами */
  fun <T> mediumTween(): TweenSpec<T> = tween(durationMillis = mediumMillis, easing = emphasized)

  /** Длинные перемещения */
  fun <T> largeTween(): TweenSpec<T> = tween(durationMillis = largeMillis, easing = decelerate)
}

internal val LocalAppMotion = staticCompositionLocalOf<AppMotion> {
  error("No AppMotion provided: оберните экран в AppTheme")
}
