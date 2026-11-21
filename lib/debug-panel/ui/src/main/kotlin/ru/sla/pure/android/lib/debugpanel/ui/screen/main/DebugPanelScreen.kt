package ru.sla.pure.android.lib.debugpanel.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.uikit.component.topappbar.TopAppBar
import ru.sla.pure.android.uikit.spacer.VSpacer
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun DebugPanelScreen(viewModel: DebugPanelViewModel) {
  val buildType by viewModel.collectAsState()
  BackHandler(onBack = viewModel::onCloseRequested)

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = AppTheme.colors.backgroundPrimary,
    contentColor = AppTheme.colors.contentPrimary,
  ) {
    Column(modifier = Modifier.safeDrawingPadding()) {
      TopAppBar(
        title = { Text(stringResource(R.string.debug_panel_title)) },
        actions = {
          TextButton(onClick = viewModel::onCloseRequested) {
            Text(stringResource(R.string.debug_panel_close))
          }
        }
      )
      VSpacer(16.dp)
      Text(
        text = stringResource(R.string.debug_panel_build_type, buildType),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }
  }
}
