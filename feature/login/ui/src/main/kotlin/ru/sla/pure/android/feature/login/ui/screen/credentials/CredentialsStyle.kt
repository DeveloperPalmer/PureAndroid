package ru.sla.pure.android.feature.login.ui.screen.credentials

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.sla.pure.android.feature.login.ui.R

internal data class CredentialsColors(
  val backgroundPrimary: Color,
  val contentPrimary: Color,
  val contentSecondary: Color,
  val cardSecondary: Color,
  val buttonSecondaryBg: Color,
)

internal val PrimaryButtonColor = Color(0xFF7520FF)

internal val LightCredentialsColors = CredentialsColors(
  backgroundPrimary = Color.White,
  contentPrimary = Color(0xFF1C1E24),
  contentSecondary = Color(0xFF4A4E5D),
  cardSecondary = Color(0xFFF5F5F5),
  buttonSecondaryBg = Color.White,
)

internal val DarkCredentialsColors = CredentialsColors(
  backgroundPrimary = Color(0xFF1D1D1D),
  contentPrimary = Color.White,
  contentSecondary = Color(0xFFE7E7E7),
  cardSecondary = Color(0xFF404040),
  buttonSecondaryBg = Color(0xFF404040),
)

internal object CredentialsTypography {
  private val roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
  )

  private val defaultFontPadding = PlatformTextStyle(includeFontPadding = false)

  val display3 = TextStyle(
    fontFamily = roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = 0.sp,
    platformStyle = defaultFontPadding,
  )

  val body1 = TextStyle(
    fontFamily = roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp,
    platformStyle = defaultFontPadding,
  )

  val body3 = TextStyle(
    fontFamily = roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
    platformStyle = defaultFontPadding,
  )

  val title3 = TextStyle(
    fontFamily = roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
    platformStyle = defaultFontPadding,
  )
}
