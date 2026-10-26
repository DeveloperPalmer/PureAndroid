package ru.sla.pure.android.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Цвета по ролям: экран говорит «основной текст» или «акцент», а не «серый 900». Тема меняет
 * только то, какой цвет палитры стоит за ролью, поэтому светлая и тёмная схемы переключаются
 * без правок в экранах
 */
@Immutable
data class AppColors(
    val isLight: Boolean,
    /** Фон экрана */
    val backgroundPrimary: Color,
    /** Фон второго уровня: секции, подложки */
    val backgroundSecondary: Color,
    /** Поверхности поверх фона: панели, карточки */
    val surfacePrimary: Color,
    /** Основной текст и иконки */
    val contentPrimary: Color,
    /** Вспомогательный текст */
    val contentSecondary: Color,
    /** Подсказки и недоступные элементы */
    val contentTertiary: Color,
    /** Акцент: кнопки, выбранные элементы */
    val accentPrimary: Color,
    /** Мягкий акцент: подложка выбранного элемента */
    val accentSecondary: Color,
    /** Текст и иконки поверх [accentPrimary] */
    val contentOnAccent: Color,
    /** Разделители и обводки */
    val divider: Color,
    val errorPrimary: Color,
    val successPrimary: Color,
)

internal val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided: оберните экран в AppTheme")
}
