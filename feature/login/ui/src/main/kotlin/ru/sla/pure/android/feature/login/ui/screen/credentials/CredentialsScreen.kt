package ru.sla.pure.android.feature.login.ui.screen.credentials

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.sla.pure.android.feature.login.ui.R
import ru.sla.pure.android.uikit.spacer.HSpacer
import ru.sla.pure.android.uikit.spacer.VSpacer

@Composable
fun CredentialsScreen(
  googleInProgress: Boolean,
  onSignInByEmail: () -> Unit,
  onSignInByGoogle: () -> Unit,
) {
  CredentialsContent(
    colors = if (isSystemInDarkTheme()) DarkCredentialsColors else LightCredentialsColors,
    googleInProgress = googleInProgress,
    onSignInByEmail = onSignInByEmail,
    onSignInByGoogle = onSignInByGoogle,
  )
}

@Composable
private fun CredentialsContent(
  colors: CredentialsColors,
  googleInProgress: Boolean,
  onSignInByEmail: () -> Unit,
  onSignInByGoogle: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.backgroundPrimary)
      .systemBarsPadding()
      .displayCutoutPadding(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    VSpacer(24.dp)
    Text(
      modifier = Modifier.padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter),
      color = colors.contentPrimary,
      style = CredentialsTypography.display3,
    )
    VSpacer(12.dp)
    Text(
      modifier = Modifier.padding(horizontal = 48.dp),
      text = stringResource(R.string.credentials_enter_description),
      color = colors.contentSecondary,
      style = CredentialsTypography.body1,
      textAlign = TextAlign.Center,
    )
    VSpacer(24.dp)
    CredentialsButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter_by_google),
      containerColor = PrimaryButtonColor,
      contentColor = Color.White,
      showLoading = googleInProgress,
      onClick = onSignInByGoogle,
    )
    VSpacer(16.dp)
    OptionsSeparator(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      colors = colors,
    )
    VSpacer(16.dp)
    CredentialsButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter_by_email),
      containerColor = colors.buttonSecondaryBg,
      contentColor = colors.contentPrimary,
      border = BorderStroke(Dp.Hairline, colors.cardSecondary),
      onClick = onSignInByEmail,
    )
  }
}

@Composable
private fun OptionsSeparator(
  modifier: Modifier,
  colors: CredentialsColors,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = colors.cardSecondary)
    HSpacer(16.dp)
    Text(
      text = stringResource(R.string.credentials_or),
      color = colors.contentSecondary,
      style = CredentialsTypography.body3,
    )
    HSpacer(16.dp)
    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = colors.cardSecondary)
  }
}

@Composable
private fun CredentialsButton(
  modifier: Modifier,
  text: String,
  containerColor: Color,
  contentColor: Color,
  onClick: () -> Unit,
  showLoading: Boolean = false,
  border: BorderStroke? = null,
) {
  Surface(
    onClick = { if (!showLoading) onClick() },
    modifier = modifier
      .heightIn(min = 48.dp)
      .semantics { role = Role.Button },
    shape = RoundedCornerShape(16.dp),
    border = border,
    color = containerColor,
    contentColor = contentColor,
  ) {
    val contentAlpha by animateFloatAsState(
      targetValue = if (showLoading) 0f else 1f,
      label = "buttonContentAlpha",
    )
    Box(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        modifier = Modifier.alpha(contentAlpha),
        text = text,
        style = CredentialsTypography.title3,
        color = contentColor,
      )
      AnimatedVisibility(visible = showLoading, enter = fadeIn(), exit = fadeOut()) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = contentColor,
          strokeWidth = 2.dp,
        )
      }
    }
  }
}
