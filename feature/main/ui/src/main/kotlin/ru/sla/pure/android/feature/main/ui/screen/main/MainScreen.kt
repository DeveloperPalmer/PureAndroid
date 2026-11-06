package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
fun MainScreen(viewModel: MainViewModel) {
  val state by viewModel.collectAsState()
  val characters = state.characters.collectAsLazyPagingItems()

  LaunchedEffect(characters, viewModel) {
    snapshotFlow { characters.loadState }.collect { loadStates ->
      viewModel.onLoadStatesChanged(loadStates = loadStates)
    }
  }

  LaunchedEffect(characters, viewModel) {
    snapshotFlow { characters.itemCount }.collect { count ->
      viewModel.onCharactersCountChanged(count = count)
    }
  }

  MainContent(
    characters = characters,
    contentLoadState = state.contentLoadState,
    refreshInProgress = state.refreshInProgress,
    onOpenCharacter = { id -> viewModel.onOpenCharacter(id) },
  )
}

@Composable
internal fun MainContent(
  modifier: Modifier = Modifier,
  characters: LazyPagingItems<Character>,
  contentLoadState: ContentLoadState,
  onOpenCharacter: (Int) -> Unit,
  refreshInProgress: Boolean = false,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
      .windowInsetsPadding(insets = WindowInsets.safeDrawing),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(id = R.string.characters_title),
        style = AppTheme.typography.headline,
      )
    }
    PullToRefreshBox(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      isRefreshing = refreshInProgress,
      onRefresh = characters::refresh,
    ) {
      if (characters.itemCount == 0) {
        EmptyContent(
          modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
          characters = characters,
          contentLoadState = contentLoadState
        )
      } else {
        ReadyContent(
          modifier = Modifier
            .fillMaxSize()
            .testTag(tag = "characters"),
          characters = characters,
          contentLoadState = contentLoadState,
          onOpenCharacter = onOpenCharacter,
        )
      }
    }
  }
}

@Composable
private fun EmptyContent(
  modifier: Modifier = Modifier,
  characters: LazyPagingItems<Character>,
  contentLoadState: ContentLoadState,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    when (contentLoadState) {
      is ContentLoadState.Loading -> {
        CircularProgressIndicator()
      }
      is ContentLoadState.Error -> {
        Text(
          text = stringResource(contentLoadState.error.messageResource),
        )
        Button(
          onClick = characters::retry,
        ) {
          Text(
            text = stringResource(R.string.characters_retry),
          )
        }
      }
      is ContentLoadState.Ready -> {
        Text(
          text = stringResource(R.string.characters_empty),
        )
      }
    }
  }
}

@Composable
private fun ReadyContent(
  modifier: Modifier = Modifier,
  characters: LazyPagingItems<Character>,
  contentLoadState: ContentLoadState,
  onOpenCharacter: (Int) -> Unit,
) {
  LazyColumn(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    items(
      key = characters.itemKey { character -> character.id },
      count = characters.itemCount,
    ) { index ->
      characters[index]?.let { character ->
        CharacterRow(
          character = character,
          onClick = { onOpenCharacter(character.id) }
        )
      }
    }
    item(key = "load-state") {
      BottomLoading(
        characters = characters,
        contentLoadState = contentLoadState
      )
    }
  }
}

@Composable
private fun CharacterRow(
  character: Character,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
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
      text = stringResource(R.string.character_description, status, character.species),
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

@Composable
private fun BottomLoading(
  modifier: Modifier = Modifier,
  characters: LazyPagingItems<Character>,
  contentLoadState: ContentLoadState,
) {
  val loadingDescription = stringResource(R.string.characters_loading)
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(all = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(space = 12.dp),
  ) {
    when (contentLoadState) {
      is ContentLoadState.Ready -> {
        Unit
      }
      is ContentLoadState.Loading -> {
        CircularProgressIndicator(
          modifier = Modifier.semantics {
            contentDescription = loadingDescription
          },
        )
      }
      is ContentLoadState.Error -> {
        Text(
          text = stringResource(contentLoadState.error.messageResource),
        )
        Button(
          onClick = characters::retry,
        ) {
          Text(
            text = stringResource(R.string.characters_retry),
          )
        }
      }
    }
  }
}
