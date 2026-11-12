package ru.sla.pure.android.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

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

  val elevation: AppElevation
    @Composable
    @ReadOnlyComposable
    get() = LocalAppElevation.current
}

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
  val elevation = remember { AppElevation() }
  CompositionLocalProvider(
    LocalAppColors provides colors,
    LocalAppTypography provides typography,
    LocalAppShapes provides shapes,
    LocalAppMotion provides motion,
    LocalAppElevation provides elevation,
  ) {
    MaterialTheme(
      colorScheme = colors.toMaterialColorScheme(),
      typography = typography.toMaterialTypography(),
      shapes = shapes.toMaterialShapes(),
    ) {
      // MaterialTheme supplies selection colors too, so override inside its content.
      CompositionLocalProvider(
        LocalContentColor provides colors.contentPrimary,
        LocalTextSelectionColors provides TextSelectionColors(
          handleColor = colors.contentAccentPrimary,
          backgroundColor = colors.backgroundAccentPrimary,
        ),
        content = content,
      )
    }
  }
}

private fun AppColors.toMaterialColorScheme(): ColorScheme {
  val scheme = if (isLight) lightColorScheme() else darkColorScheme()
  return scheme.copy(
    primaryFixed = LightColors.backgroundAccentPrimary,
    primaryFixedDim = LightColors.contentAccentReadable,
    onPrimaryFixed = LightColors.contentPrimary,
    onPrimaryFixedVariant = LightColors.contentSecondary,
    secondaryFixed = LightColors.cardSecondary,
    secondaryFixedDim = LightColors.cardQuinary,
    onSecondaryFixed = LightColors.contentPrimary,
    onSecondaryFixedVariant = LightColors.contentSecondary,
    tertiaryFixed = LightColors.contentBlue,
    tertiaryFixedDim = LightColors.contentBlue,
    onTertiaryFixed = LightColors.contentAccentDark,
    onTertiaryFixedVariant = LightColors.contentAccentDark,
    primary = buttonPrimaryBg,
    onPrimary = buttonPrimaryContent,
    primaryContainer = backgroundAccentPrimary,
    onPrimaryContainer = contentPrimary,
    inversePrimary = contentAccentReadable,
    secondary = contentAccentReadable,
    onSecondary = backgroundPrimary,
    secondaryContainer = backgroundAccentPrimary,
    onSecondaryContainer = contentPrimary,
    tertiary = contentBlue,
    onTertiary = contentAccentDark,
    tertiaryContainer = cardSecondary,
    onTertiaryContainer = contentPrimary,
    background = backgroundPrimary,
    onBackground = contentPrimary,
    surface = backgroundPrimary,
    onSurface = contentPrimary,
    surfaceVariant = cardSecondary,
    onSurfaceVariant = contentSecondary,
    surfaceTint = contentAccentPrimary,
    inverseSurface = contentPrimary,
    inverseOnSurface = backgroundPrimary,
    error = errorPrimary,
    onError = contentAccentSecondary,
    errorContainer = errorSecondary,
    onErrorContainer = contentPrimary,
    outline = contentQuaternary,
    outlineVariant = cardQuinary,
    scrim = backgroundTertiary,
    surfaceBright = cardSecondary,
    surfaceDim = backgroundSecondary,
    surfaceContainerLowest = backgroundSecondary,
    surfaceContainerLow = cardTertiary,
    surfaceContainer = cardPrimary,
    surfaceContainerHigh = cardSecondary,
    surfaceContainerHighest = cardQuinary,
  )
}

private fun AppTypography.toMaterialTypography(): Typography = Typography(
  displayLarge = display1,
  displayMedium = display2,
  displaySmall = display3,
  headlineLarge = headline1,
  headlineMedium = headline2,
  headlineSmall = headline3,
  titleLarge = title1,
  titleMedium = title2,
  titleSmall = title3,
  bodyLarge = body1,
  bodyMedium = body2,
  bodySmall = body3,
  labelLarge = label1,
  labelMedium = label2,
  labelSmall = label3,
)

private fun AppShapes.toMaterialShapes(): Shapes = Shapes(
  extraSmall = round12,
  small = round12,
  medium = round16,
  large = round24,
  extraLarge = round32,
)
