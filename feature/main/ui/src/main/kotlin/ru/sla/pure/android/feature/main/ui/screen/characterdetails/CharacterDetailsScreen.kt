package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun CharacterDetailsScreen(viewModel: CharacterDetailsViewModel) {
  val state by viewModel.collectAsState()

  CharacterDetailsContent(
    state = state,
    onNavigateBack = viewModel::onNavigateBack,
    onRetry = viewModel::onRetry,
  )
}

@Composable
internal fun CharacterDetailsContent(
  state: ViewState,
  onNavigateBack: () -> Unit,
  onRetry: () -> Unit,
) {
  Column(
    modifier = Modifier
      .testTag(CharacterDetailsTestTags.SCREEN)
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    TextButton(onClick = onNavigateBack) {
      Text(stringResource(R.string.character_details_back))
    }
    val character = state.character
    when (val loadState = state.contentLoadState) {
      is ContentLoadState.Loading -> {
        CircularProgressIndicator()
      }
      is ContentLoadState.Error -> {
        Text(
          text = stringResource(loadState.error.messageResource),
          modifier = Modifier.testTag(CharacterDetailsTestTags.DETAILS_ERROR),
        )
        Button(onClick = onRetry) {
          Text(stringResource(R.string.characters_retry))
        }
      }
      is ContentLoadState.Ready -> {
        if (character == null) {
          CircularProgressIndicator()
        } else {
          Text(
            text = character.name,
            modifier = Modifier.testTag(CharacterDetailsTestTags.DETAILS_NAME),
            style = AppTheme.typography.headline,
          )
          Text(text = character.species, modifier = Modifier.testTag(CharacterDetailsTestTags.DETAILS_SPECIES))
          Text(text = character.origin.name, modifier = Modifier.testTag(CharacterDetailsTestTags.DETAILS_ORIGIN))
          Text(text = character.location.name, modifier = Modifier.testTag(CharacterDetailsTestTags.DETAILS_LOCATION))
        }
      }
    }
  }
}
