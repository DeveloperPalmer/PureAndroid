package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.entity.UiError
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreenTest.Companion.characters
import ru.sla.pure.android.uikit.theme.AppTheme
import java.io.IOException

private const val PAGE_SIZE = 20

@OptIn(ExperimentalTestApi::class)
class MainScreenTest : BaseTestCase() {
  @Test
  @DisplayName("Первичная загрузка показывает спиннер контента без индикатора потягивания")
  fun initialLoadingShowsContentSpinnerWithoutRefreshIndicator() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = LoadState.Loading),
        contentLoadState = ContentLoadState.Loading,
        refreshInProgress = false,
      )
      val screen = MainScreen(this)

      screen.loadingIndicator.assertIsDisplayed()
      // Индикатор потягивания — тоже indeterminate progress; на экране должен быть ровно один
      assertEquals(1, indeterminateProgressCount())
    }
  }

  @Test
  @DisplayName("Успешная загрузка показывает список персонажей")
  fun successfulLoadShowsCharacters() {
    runScreenTest {
      showMainContent(staticContent(characters(1..PAGE_SIZE)))

      MainScreen(this).character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("После первичной загрузки список показан с начала")
  fun initialLoadShowsListFromStart() {
    runScreenTest {
      // Регрессия на якорь скролла: раньше список открывался на футере загрузки
      showMainContent(staticContent(characters(1..PAGE_SIZE * 2)))
      val screen = MainScreen(this)

      screen.character("Персонаж 1").assertIsDisplayed()
      onNodeWithTag("characters").assert(
        SemanticsMatcher("Список находится в начале") { node ->
          node.config[SemanticsProperties.VerticalScrollAxisRange].value() == 0f
        },
      )
    }
  }

  @Test
  @DisplayName("Индикатор потягивания виден поверх загруженного списка")
  fun refreshIndicatorIsVisibleOverLoadedList() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(characters(1..PAGE_SIZE)),
        refreshInProgress = true,
      )
      val screen = MainScreen(this)

      // В готовом контенте своих спиннеров нет — единственный индикатор это PTR
      assertEquals(1, indeterminateProgressCount())
      screen.character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("У индикатора загрузки есть описание для программы чтения с экрана")
  fun loadingIndicatorHasAccessibleDescription() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = LoadState.Loading),
        contentLoadState = ContentLoadState.Loading,
      )

      // Нода найдётся только если у спиннера есть contentDescription
      MainScreen(this).loadingIndicator.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Ошибка первичной загрузки показывает текст ошибки и кнопку повторить")
  fun initialLoadErrorShowsMessageAndRetry() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = connectionError()),
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = MainScreen(this)

      screen.error(R.string.error_connection).assertIsDisplayed()
      screen.retryButton.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Повторить после ошибки перезапускает загрузку и показывает контент")
  fun retryAfterErrorLoadsCharacters() {
    runScreenTest {
      // Источник падает один раз, затем отдаёт страницу: retry вызывает реальную перезагрузку
      showMainContent(
        charactersFlow = Pager(PagingConfig(pageSize = PAGE_SIZE)) { FailOncePagingSource() }.flow,
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = MainScreen(this)

      screen.retryButton.assertIsDisplayed().also { screen.retryButton.performClick() }

      screen.character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Пустая страница предлагает обновить")
  fun emptyPagePromptsRefresh() {
    runScreenTest {
      showMainContent(staticContent(emptyList()))

      MainScreen(this).emptyMessage.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Прокрутка к концу списка автоматически загружает следующую страницу")
  fun scrollingToEndLoadsNextPage() {
    runScreenTest {
      showMainContent(Pager(PagingConfig(pageSize = PAGE_SIZE)) { TwoPagesPagingSource() }.flow)
      val screen = MainScreen(this)

      onNodeWithTag("characters").performScrollToIndex(PAGE_SIZE * 2 - 1)

      screen.character("Персонаж ${PAGE_SIZE * 2}").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Ошибка догрузки показывает ошибку в футере списка, а не на весь экран")
  fun appendErrorShowsInListFooter() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(characters(1..PAGE_SIZE), append = connectionError()),
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = MainScreen(this)

      // Контент остаётся на экране, ошибка — в футере
      screen.character("Персонаж 1").assertIsDisplayed()

      // Футер изначально за пределами экрана — докручиваем до него
      onNodeWithTag("characters").performScrollToIndex(PAGE_SIZE)
      screen.error(R.string.error_connection).assertIsDisplayed()
      screen.retryButton.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("Потягивание обновляет данные списка")
  fun pullToRefreshLoadsNewCharacters() {
    runScreenTest {
      // Каждое поколение источника отдаёт новые имена: refresh создаёт источник заново
      var generation = 0
      showMainContent(
        Pager(PagingConfig(pageSize = PAGE_SIZE)) { GeneratingPagingSource(++generation) }.flow,
      )
      val screen = MainScreen(this)
      screen.character("Поколение 1 №1").assertIsDisplayed()

      onNodeWithTag("characters").performTouchInput { swipeDown() }

      screen.character("Поколение 2 №1").assertIsDisplayed()
    }
  }

  // --- Окружение ---

  private fun AndroidComposeUiTest<ComponentActivity>.showMainContent(
    charactersFlow: Flow<PagingData<Character>>,
    contentLoadState: ContentLoadState = ContentLoadState.Ready,
    refreshInProgress: Boolean = false,
  ) {
    setContent {
      AppTheme {
        MainContent(
          characters = charactersFlow.collectAsLazyPagingItems(),
          contentLoadState = contentLoadState,
          refreshInProgress = refreshInProgress,
          onOpenCharacter = {},
        )
      }
    }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.indeterminateProgressCount(): Int {
    return onAllNodes(expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate))
      .fetchSemanticsNodes()
      .size
  }

  private fun staticContent(
    characters: List<Character>,
    refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = false),
    append: LoadState = LoadState.NotLoading(endOfPaginationReached = false),
  ): Flow<PagingData<Character>> {
    return flowOf(
      PagingData.from(
        data = characters,
        sourceLoadStates = LoadStates(
          refresh = refresh,
          prepend = LoadState.NotLoading(endOfPaginationReached = true),
          append = append,
        ),
      )
    )
  }

  private fun connectionError(): LoadState.Error {
    return LoadState.Error(IOException("no connection"))
  }

  private fun connectionUiError(): UiError {
    return UiError(messageResource = R.string.error_connection)
  }

  companion object {
    fun characters(range: IntRange, namePrefix: String = "Персонаж "): List<Character> {
      return range.map { id ->
        Character(
          id,
          "$namePrefix$id",
          CharacterStatus.Alive,
          "Human",
          "",
          CharacterGender.Male,
          CharacterLocation("Earth", ""),
          CharacterLocation("Citadel", ""),
          "",
        )
      }
    }
  }
}

/** Падает на первой загрузке, дальше отдаёт первую страницу. */
private class FailOncePagingSource : PagingSource<Int, Character>() {
  private var failed = false
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
    if (!failed) {
      failed = true
      return LoadResult.Error(IOException("no connection"))
    }
    return LoadResult.Page(data = characters(1..PAGE_SIZE), prevKey = null, nextKey = null)
  }

  override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
    return null
  }
}

/** Две полные страницы: вторая грузится догрузкой. */
private class TwoPagesPagingSource : PagingSource<Int, Character>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
    val page = params.key ?: 1
    val from = (page - 1) * PAGE_SIZE + 1
    return LoadResult.Page(
      data = characters(from..<from + PAGE_SIZE),
      prevKey = if (page > 1) page - 1 else null,
      nextKey = if (page < 2) page + 1 else null,
    )
  }

  override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
    return null
  }
}

/** Каждое поколение возвращает свои имена — видно, что refresh реально пересоздал источник. */
private class GeneratingPagingSource(private val generation: Int) : PagingSource<Int, Character>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
    return LoadResult.Page(
      data = characters(1..PAGE_SIZE, namePrefix = "Поколение $generation №"),
      prevKey = null,
      nextKey = null,
    )
  }

  override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
    return null
  }
}
