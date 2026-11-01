package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
  val data = remember {
    flowOf(
      PagingData.from(
        data = listOf(
          Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.Alive,
            species = "Human",
            type = "",
            gender = CharacterGender.Male,
            origin = CharacterLocation(
              name = "Earth",
              url = "",
            ),
            location = CharacterLocation(
              name = "Citadel of Ricks",
              url = "",
            ),
            imageUrl = "",
          )
        ),
        sourceLoadStates = LoadStates(
          refresh = LoadState.NotLoading(endOfPaginationReached = true),
          prepend = LoadState.NotLoading(endOfPaginationReached = true),
          append = LoadState.NotLoading(endOfPaginationReached = true),
        ),
      )
    )
  }
  AppTheme {
    MainContent(
      characters = data.collectAsLazyPagingItems(),
      contentLoadState = ContentLoadState.Ready,
    )
  }
}
