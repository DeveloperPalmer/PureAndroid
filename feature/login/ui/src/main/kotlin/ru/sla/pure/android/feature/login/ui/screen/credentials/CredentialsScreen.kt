package ru.sla.pure.android.feature.login.ui.screen.credentials

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.feature.login.ui.R
import ru.sla.pure.android.uikit.component.button.PrimaryButton
import ru.sla.pure.android.uikit.component.button.SecondaryButton
import ru.sla.pure.android.uikit.spacer.HSpacer
import ru.sla.pure.android.uikit.spacer.VSpacer
import ru.sla.pure.android.uikit.theme.AppColors
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun CredentialsScreen(viewModel: CredentialsViewModel) {
  val state by viewModel.collectAsState()
  CredentialsContent(
    state = state,
    onSignInByEmail = { viewModel.onSignInByEmail() },
    onSignInByGoogle = { viewModel.onSignInByGoogle() },
  )
}

@Composable
internal fun CredentialsContent(
  state: CredentialsState,
  onSignInByEmail: () -> Unit,
  onSignInByGoogle: () -> Unit,
) {
  val colors = AppTheme.colors
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    VSpacer(24.dp)
    Text(
      modifier = Modifier.padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter),
      color = colors.contentPrimary,
      style = AppTheme.typography.display3,
    )
    VSpacer(12.dp)
    Text(
      modifier = Modifier.padding(horizontal = 48.dp),
      text = stringResource(R.string.credentials_enter_description),
      color = colors.contentSecondary,
      style = AppTheme.typography.body1,
      textAlign = TextAlign.Center,
    )
    VSpacer(24.dp)
    PrimaryButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter_by_google),
      showLoading = state.googleInProgress,
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
    SecondaryButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(R.string.credentials_enter_by_email),
      onClick = onSignInByEmail,
    )
    state.message?.let { message ->
      VSpacer(16.dp)
      Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = stringResource(message),
        color = colors.contentPrimary,
        style = AppTheme.typography.body1,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun OptionsSeparator(
  modifier: Modifier,
  colors: AppColors,
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
      style = AppTheme.typography.body3,
    )
    HSpacer(16.dp)
    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = colors.cardSecondary)
  }
}
