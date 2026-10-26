package ru.sla.pure.android.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

/** Токены темы для экранов и компонентов: `AppTheme.colors.contentPrimary`, `AppTheme.typography.body` */
object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val motion: AppMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalAppMotion.current
}

/**
 * Тема приложения. Кладёт токены в `CompositionLocal`, а заодно настраивает `MaterialTheme` из тех же
 * токенов: пока экраны пользуются компонентами Material 3 (кнопки, панель, снэкбар), они тоже
 * берут цвета, шрифты и формы дизайн-системы
 */
@Composable
fun AppTheme(
    colorTheme: ColorTheme = if (isSystemInDarkTheme()) ColorTheme.Dark else ColorTheme.Light,
    content: @Composable () -> Unit,
) {
    val colors = when (colorTheme) {
        ColorTheme.Light -> LightColors
        ColorTheme.Dark -> DarkColors
    }
    val typography = remember { AppTypography() }
    val shapes = remember { AppShapes() }
    val motion = remember { AppMotion() }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppShapes provides shapes,
        LocalAppMotion provides motion,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content,
        )
    }
}

private fun AppColors.toMaterialColorScheme(): ColorScheme {
    val scheme = if (isLight) lightColorScheme() else darkColorScheme()
    return scheme.copy(
        primary = accentPrimary,
        onPrimary = contentOnAccent,
        primaryContainer = accentSecondary,
        onPrimaryContainer = contentPrimary,
        // Индикатор выбранной вкладки в NavigationBar
        secondaryContainer = accentSecondary,
        onSecondaryContainer = contentPrimary,
        background = backgroundPrimary,
        onBackground = contentPrimary,
        surface = backgroundPrimary,
        onSurface = contentPrimary,
        onSurfaceVariant = contentSecondary,
        // Фон NavigationBar
        surfaceContainer = surfacePrimary,
        // Фон и текст снэкбара
        inverseSurface = contentPrimary,
        inverseOnSurface = backgroundPrimary,
        error = errorPrimary,
        outline = divider,
        outlineVariant = divider,
    )
}

private fun AppTypography.toMaterialTypography(): Typography = Typography(
    headlineSmall = headline,
    titleMedium = title,
    bodyLarge = body,
    bodyMedium = bodySmall,
    labelLarge = label,
    labelMedium = caption,
)

private fun AppShapes.toMaterialShapes(): Shapes = Shapes(
    small = round8,
    medium = round12,
    large = round16,
    extraLarge = round24,
)
