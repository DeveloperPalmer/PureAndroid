package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun loadingHasAccessibleDescription() {
    show(
      flowOf(
        PagingData.from(
          emptyList(),
          LoadStates(
            LoadState.Loading,
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
          )
        )
      )
    )
    compose.onNodeWithContentDescription("Загрузка персонажей").assertIsDisplayed()
  }

  @Test
  fun emptyPageOffersRefresh() {
    show(
      flowOf(
        PagingData.from(
          emptyList(),
          LoadStates(
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
          )
        )
      )
    )
    compose.onNodeWithText("Персонажей пока нет").assertIsDisplayed()
    compose.onNodeWithText("Обновить").assertIsDisplayed()
  }

  @Test
  fun retryReloadsFailedPage() {
    val calls = AtomicInteger()
    show(
      pager { page ->
        if (calls.incrementAndGet() == 1) throw ServerException.Unreachable(null)
        listOf(character(page))
      }
    )
    compose.onNodeWithText("Повторить").performClick()
    compose.onNodeWithText("Character 1").assertIsDisplayed()
    assertTrue(calls.get() >= 2)
  }

  @Test
  fun scrollingAutomaticallyLoadsNextPage() {
    val pages = AtomicInteger()
    show(
      pager { page ->
        pages.updateAndGet { maxOf(it, page) }
        ((page - 1) * 20 + 1..page * 20).map(::character)
      }
    )
    compose.onNodeWithText("Character 1").assertIsDisplayed()
    compose.onNodeWithTag("characters").performScrollToIndex(19)
    compose.waitUntil(10_000) { pages.get() == 2 }
    compose.onNodeWithTag("characters").performScrollToIndex(20)
    compose.onNodeWithText("Character 21").assertIsDisplayed()
  }

  private fun show(flow: Flow<PagingData<Character>>) {
    compose.setContent { AppTheme { MainContent(flow.collectAsLazyPagingItems()) } }
  }

  private fun pager(read: (Int) -> List<Character>): Flow<PagingData<Character>> = Pager(
    PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5, enablePlaceholders = false),
  ) {
    object : PagingSource<Int, Character>() {
      override fun getRefreshKey(state: PagingState<Int, Character>): Int? = null
      override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        return try {
          LoadResult.Page(read(page), null, if (page < 2) page + 1 else null)
        } catch (error: Exception) {
          LoadResult.Error(error)
        }
      }
    }
  }.flow
}

private fun character(id: Int) = Character(
  id,
  "Character $id",
  CharacterStatus.Alive,
  "Human",
  "",
  CharacterGender.Male,
  CharacterLocation("Earth", ""),
  CharacterLocation("Citadel", ""),
  "",
)
