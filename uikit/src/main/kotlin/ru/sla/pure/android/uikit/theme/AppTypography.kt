package ru.sla.pure.android.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Стили текста по назначению. Шрифт системный: свой шрифт подключается одной `FontFamily`
 * в этом файле, экраны при этом не меняются
 */
@Immutable
class AppTypography {
  /** Заголовок экрана */
  val headline = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 32.sp)

  /** Заголовок блока или панели */
  val title = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)

  /** Основной текст */
  val body = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)

  /** Второстепенный текст */
  val bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)

  /** Текст кнопок */
  val label = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)

  /** Подписи: вкладки, метки */
  val caption = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
}

internal val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
  error("No AppTypography provided: оберните экран в AppTheme")
}
