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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val state by viewModel.collectAsState()
    MainContent(state, viewModel::refresh, viewModel::loadMore, viewModel::retry, modifier)
}

@Composable
internal fun MainContent(
    state: ViewState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                TextButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Text(stringResource(R.string.characters_refresh))
                }
            }
            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                items(state.characters, key = { it.id }) { character ->
                    CharacterRow(character)
                }
                item(key = "load-state") {
                    Column(
                        Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        when {
                            state.isLoading -> {
                                val label = stringResource(R.string.characters_loading)
                                CircularProgressIndicator(Modifier.semantics { contentDescription = label })
                            }
                            state.error != null -> {
                                Text(stringResource(when (state.error) {
                                    LoadError.Connection -> R.string.characters_error_connection
                                    LoadError.Server -> R.string.characters_error_server
                                    LoadError.Unexpected -> R.string.characters_error_unexpected
                                }))
                                Button(onClick = onRetry) { Text(stringResource(R.string.characters_retry)) }
                            }
                            state.characters.isEmpty() -> Text(stringResource(R.string.characters_empty))
                            state.nextPage != null -> Button(onClick = onLoadMore) {
                                Text(stringResource(R.string.characters_more))
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
        val status = stringResource(when (character.status) {
            CharacterStatus.Alive -> R.string.character_alive
            CharacterStatus.Dead -> R.string.character_dead
            CharacterStatus.Unknown -> R.string.character_unknown
        })
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
    AppTheme {
        MainContent(
            state = ViewState(
                characters = listOf(Character(
                    1, "Rick Sanchez", CharacterStatus.Alive, "Human", "", CharacterGender.Male,
                    CharacterLocation("Earth", ""), CharacterLocation("Citadel of Ricks", ""), "",
                )), isLoading = false, nextPage = 2,
            ), onRefresh = {}, onLoadMore = {}, onRetry = {},
        )
    }
}
