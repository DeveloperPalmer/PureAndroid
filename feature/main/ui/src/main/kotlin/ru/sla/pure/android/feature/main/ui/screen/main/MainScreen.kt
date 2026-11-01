package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainViewModel,
) {
  val state by viewModel.collectAsState()
  val characters = state.characters.collectAsLazyPagingItems()
  LaunchedEffect(
    key1 = characters,
    key2 = viewModel,
  ) {
    snapshotFlow {
      characters.loadState
    }.collect { loadStates ->
      viewModel.onLoadStatesChanged(loadStates = loadStates)
    }
  }
  MainContent(
    characters = characters,
    contentLoadState = state.contentLoadState,
    modifier = modifier,
    refreshInProgress = state.refreshInProgress,
  )
}

@Composable
internal fun MainContent(
  characters: LazyPagingItems<Character>,
  contentLoadState: ContentLoadState,
  modifier: Modifier = Modifier,
  refreshInProgress: Boolean = false,
) {
  Surface(
    modifier = modifier.fillMaxSize(),
    color = AppTheme.colors.backgroundPrimary,
    contentColor = AppTheme.colors.contentPrimary,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(insets = WindowInsets.safeDrawing),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = stringResource(id = R.string.characters_title),
          style = AppTheme.typography.headline,
        )
        TextButton(
          onClick = characters::refresh,
          enabled = !refreshInProgress,
        ) {
          Text(
            text = stringResource(id = R.string.characters_refresh),
          )
        }
      }
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag(tag = "characters"),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        items(
          key = characters.itemKey { character -> character.id },
          count = characters.itemCount,
        ) { index ->
          characters[index]?.let { character ->
            CharacterRow(character = character)
          }
        }
        item(key = "load-state") {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
          ) {
            when {
              contentLoadState is ContentLoadState.Loading -> {
                val label = stringResource(id = R.string.characters_loading)
                CircularProgressIndicator(
                  modifier = Modifier.semantics {
                    contentDescription = label
                  },
                )
              }
              contentLoadState is ContentLoadState.Error -> {
                Text(
                  text = stringResource(id = contentLoadState.error.messageResource),
                )
                Button(
                  onClick = characters::retry,
                ) {
                  Text(
                    text = stringResource(id = R.string.characters_retry),
                  )
                }
              }
              characters.itemCount == 0 -> {
                Text(
                  text = stringResource(id = R.string.characters_empty),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CharacterRow(character: Character) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(space = 4.dp),
  ) {
    Text(
      text = character.name,
      style = AppTheme.typography.title,
    )
    val status = stringResource(
      id = when (character.status) {
        CharacterStatus.Alive -> R.string.character_alive
        CharacterStatus.Dead -> R.string.character_dead
        CharacterStatus.Unknown -> R.string.character_unknown
      }
    )
    Text(
      text = stringResource(
        id = R.string.character_description,
        formatArgs = arrayOf(status, character.species),
      ),
      style = AppTheme.typography.bodySmall,
      color = AppTheme.colors.contentSecondary,
    )
    Text(
      text = character.location.name,
      style = AppTheme.typography.bodySmall,
    )
  }
  HorizontalDivider()
}
