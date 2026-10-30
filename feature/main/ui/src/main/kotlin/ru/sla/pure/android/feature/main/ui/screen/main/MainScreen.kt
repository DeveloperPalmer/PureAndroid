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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
  val state by viewModel.collectAsState()
  val characters = state.characters.collectAsLazyPagingItems()
  MainContent(characters, modifier)
}

@Composable
internal fun MainContent(
  characters: LazyPagingItems<Character>,
  modifier: Modifier = Modifier,
) {
  val refresh = characters.loadState.refresh
  val append = characters.loadState.append
  val prepend = characters.loadState.prepend
  val error = (refresh as? LoadState.Error) ?: (append as? LoadState.Error) ?: (prepend as? LoadState.Error)
  val loading = refresh is LoadState.Loading || append is LoadState.Loading || prepend is LoadState.Loading
  Surface(
    modifier = modifier.fillMaxSize(),
    color = AppTheme.colors.backgroundPrimary,
    contentColor = AppTheme.colors.contentPrimary,
  ) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(stringResource(R.string.characters_title), style = AppTheme.typography.headline)
        TextButton(onClick = characters::refresh, enabled = refresh !is LoadState.Loading) {
          Text(stringResource(R.string.characters_refresh))
        }
      }
      LazyColumn(
        Modifier.fillMaxSize().testTag("characters"),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        items(characters.itemCount, key = characters.itemKey { it.id }) { index ->
          characters[index]?.let { CharacterRow(it) }
        }
        item(key = "load-state") {
          Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            when {
              loading -> {
                val label = stringResource(R.string.characters_loading)
                CircularProgressIndicator(Modifier.semantics { contentDescription = label })
              }
              error != null -> {
                Text(
                  stringResource(
                    when (error.error) {
                      is ServerException.Unreachable -> R.string.characters_error_connection
                      is ServerException -> R.string.characters_error_server
                      else -> R.string.characters_error_unexpected
                    }
                  )
                )
                Button(onClick = characters::retry) { Text(stringResource(R.string.characters_retry)) }
              }
              characters.itemCount == 0 -> {
                Text(stringResource(R.string.characters_empty))
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
    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(character.name, style = AppTheme.typography.title)
    val status = stringResource(
      when (character.status) {
        CharacterStatus.Alive -> R.string.character_alive
        CharacterStatus.Dead -> R.string.character_dead
        CharacterStatus.Unknown -> R.string.character_unknown
      }
    )
    Text(
      stringResource(R.string.character_description, status, character.species),
      style = AppTheme.typography.bodySmall,
      color = AppTheme.colors.contentSecondary,
    )
    Text(character.location.name, style = AppTheme.typography.bodySmall)
  }
  HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
  val data = remember {
    flowOf(
      PagingData.from(
        listOf(
          Character(
            1,
            "Rick Sanchez",
            CharacterStatus.Alive,
            "Human",
            "",
            CharacterGender.Male,
            CharacterLocation("Earth", ""),
            CharacterLocation("Citadel of Ricks", ""),
            "",
          )
        ),
        sourceLoadStates = LoadStates(
          LoadState.NotLoading(true),
          LoadState.NotLoading(true),
          LoadState.NotLoading(true),
        ),
      )
    )
  }
  AppTheme { MainContent(data.collectAsLazyPagingItems()) }
}
