package ru.sla.pure.android.feature.main.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.uikit.spacer.VSpacer
import ru.sla.pure.android.uikit.spacer.WSpacer
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
  val state by viewModel.collectAsState()

  ProfileContent(
    state = state,
    onNavigateBack = viewModel::onNavigateBack,
    onLogout = viewModel::onLogout,
  )
}

@Composable
internal fun ProfileContent(
  state: ViewState,
  onNavigateBack: () -> Unit,
  onLogout: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.profile_title),
        style = AppTheme.typography.headline,
      )
      WSpacer(weight = 1f)
      TextButton(
        onClick = onNavigateBack,
        enabled = !state.logoutInProgress
      ) {
        Text(stringResource(R.string.profile_back))
      }
    }
    VSpacer(height = 24.dp)
    if (state.logoutFailed) {
      Text(
        text = stringResource(R.string.profile_logout_failed),
        color = AppTheme.colors.contentPrimary,
      )
      VSpacer(height = 16.dp)
    }
    Button(
      onClick = onLogout,
      enabled = !state.logoutInProgress,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        stringResource(
          if (state.logoutInProgress) R.string.profile_logging_out else R.string.profile_logout,
        ),
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
  AppTheme {
    ProfileContent(
      state = ViewState(),
      onNavigateBack = {},
      onLogout = {}
    )
  }
}
