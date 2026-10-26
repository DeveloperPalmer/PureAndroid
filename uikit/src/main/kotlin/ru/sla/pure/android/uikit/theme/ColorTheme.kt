package ru.sla.pure.android.uikit.theme

/** Цветовая схема приложения */
enum class ColorTheme {
    Light,
    Dark,
}

internal val LightColors = AppColors(
    isLight = true,
    backgroundPrimary = ColorPalette.gray0,
    backgroundSecondary = ColorPalette.gray50,
    surfacePrimary = ColorPalette.gray100,
    contentPrimary = ColorPalette.gray900,
    contentSecondary = ColorPalette.gray600,
    contentTertiary = ColorPalette.gray400,
    accentPrimary = ColorPalette.teal500,
    accentSecondary = ColorPalette.teal100,
    contentOnAccent = ColorPalette.gray0,
    divider = ColorPalette.gray200,
    errorPrimary = ColorPalette.red600,
    successPrimary = ColorPalette.green600,
)

internal val DarkColors = AppColors(
    isLight = false,
    backgroundPrimary = ColorPalette.gray950,
    backgroundSecondary = ColorPalette.gray900,
    surfacePrimary = ColorPalette.gray850,
    contentPrimary = ColorPalette.gray50,
    contentSecondary = ColorPalette.gray400,
    contentTertiary = ColorPalette.gray600,
    accentPrimary = ColorPalette.teal300,
    accentSecondary = ColorPalette.teal900,
    contentOnAccent = ColorPalette.gray950,
    divider = ColorPalette.gray800,
    errorPrimary = ColorPalette.red300,
    successPrimary = ColorPalette.green300,
)
