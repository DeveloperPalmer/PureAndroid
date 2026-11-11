package ru.sla.pure.android.feature.login.ui.screen.credentials

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.sla.pure.android.uikit.theme.AppTheme

@Preview
@Composable
private fun CredentialsScreenPreviewLight() {
  AppTheme {
    CredentialsContent(
      state = CredentialsState(googleInProgress = true),
      onSignInByEmail = {},
      onSignInByGoogle = {},
    )
  }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CredentialsScreenPreviewDark() {
  AppTheme {
    CredentialsContent(
      state = CredentialsState(),
      onSignInByEmail = {},
      onSignInByGoogle = {},
    )
  }
}
