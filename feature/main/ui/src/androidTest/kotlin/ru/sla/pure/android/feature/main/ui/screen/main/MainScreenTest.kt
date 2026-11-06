package ru.sla.pure.android.feature.main.ui.screen.main

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
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
import com.kaspersky.components.composesupport.core.KNode
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

@OptIn(ExperimentalTestApi::class)
class MainScreenTest : BaseTestCase() {
  @Test
  @DisplayName("первичная загрузка показывает спиннер контента без индикатора потягивания")
  fun первичная_загрузка_показывает_спиннер_контента_без_индикатора_потягивания() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = LoadState.Loading),
        contentLoadState = ContentLoadState.Loading,
        refreshInProgress = false,
      )
      val screen = mainScreen()

      screen.loadingIndicator.assertIsDisplayed()
      // Индикатор потягивания — тоже indeterminate progress; на экране должен быть ровно один
      assertEquals(1, indeterminateProgressCount())
    }
  }

  @Test
  @DisplayName("успешная загрузка показывает список персонажей")
  fun успешная_загрузка_показывает_список_персонажей() {
    runScreenTest {
      showMainContent(staticContent(characters(1..PAGE_SIZE)))

      mainScreen().character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("после первичной загрузки список показан с начала")
  fun после_первичной_загрузки_список_показан_с_начала() {
    runScreenTest {
      // Регрессия на якорь скролла: раньше список открывался на футере загрузки
      showMainContent(staticContent(characters(1..PAGE_SIZE * 2)))
      val screen = mainScreen()

      screen.character("Персонаж 1").assertIsDisplayed()
      screen.character("Персонаж ${PAGE_SIZE * 2}").assertIsNotDisplayed()
    }
  }

  @Test
  @DisplayName("индикатор потягивания виден поверх загруженного списка")
  fun индикатор_потягивания_виден_поверх_загруженного_списка() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(characters(1..PAGE_SIZE)),
        refreshInProgress = true,
      )
      val screen = mainScreen()

      // В готовом контенте своих спиннеров нет — единственный индикатор это PTR
      assertEquals(1, indeterminateProgressCount())
      screen.character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("индикатор загрузки доступен для screen reader")
  fun индикатор_загрузки_доступен_для_screen_reader() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = LoadState.Loading),
        contentLoadState = ContentLoadState.Loading,
      )

      // Нода найдётся только если у спиннера есть contentDescription
      mainScreen().loadingIndicator.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("ошибка первичной загрузки показывает текст ошибки и кнопку повторить")
  fun ошибка_первичной_загрузки_показывает_текст_ошибки_и_кнопку_повторить() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(emptyList(), refresh = connectionError()),
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = mainScreen()

      screen.text(checkNotNull(activity).getString(R.string.error_connection)).assertIsDisplayed()
      screen.retryButton.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("повторить после ошибки перезапускает загрузку и показывает контент")
  fun повторить_после_ошибки_перезапускает_загрузку_и_показывает_контент() {
    runScreenTest {
      // Источник падает один раз, затем отдаёт страницу: retry вызывает реальную перезагрузку
      showMainContent(
        charactersFlow = Pager(PagingConfig(pageSize = PAGE_SIZE)) { FailOncePagingSource() }.flow,
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = mainScreen()

      screen.retryButton.assertIsDisplayed().also { screen.retryButton.performClick() }

      screen.character("Персонаж 1").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("пустая страница предлагает обновить")
  fun пустая_страница_предлагает_обновить() {
    runScreenTest {
      showMainContent(staticContent(emptyList()))

      mainScreen().text(checkNotNull(activity).getString(R.string.characters_empty)).assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("скролл к концу списка автоматически подгружает следующую страницу")
  fun скролл_к_концу_списка_автоматически_подгружает_следующую_страницу() {
    runScreenTest {
      showMainContent(Pager(PagingConfig(pageSize = PAGE_SIZE)) { TwoPagesPagingSource() }.flow)
      val screen = mainScreen()

      onNodeWithTag("characters").performScrollToIndex(PAGE_SIZE * 2 - 1)

      screen.character("Персонаж ${PAGE_SIZE * 2}").assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("ошибка догрузки показывает ошибку в футере списка, а не на весь экран")
  fun ошибка_догрузки_показывает_ошибку_в_футере_списка_а_не_на_весь_экран() {
    runScreenTest {
      showMainContent(
        charactersFlow = staticContent(characters(1..PAGE_SIZE), append = connectionError()),
        contentLoadState = ContentLoadState.Error(connectionUiError()),
      )
      val screen = mainScreen()

      // Контент остаётся на экране, ошибка — в футере
      screen.character("Персонаж 1").assertIsDisplayed()

      // Футер изначально за пределами экрана — докручиваем до него
      onNodeWithTag("characters").performScrollToIndex(PAGE_SIZE)
      screen.text(checkNotNull(activity).getString(R.string.error_connection)).assertIsDisplayed()
      screen.retryButton.assertIsDisplayed()
    }
  }

  @Test
  @DisplayName("потягивание обновляет данные списка")
  fun потягивание_обновляет_данные_списка() {
    runScreenTest {
      // Каждое поколение источника отдаёт новые имена: refresh создаёт источник заново
      var generation = 0
      showMainContent(
        Pager(PagingConfig(pageSize = PAGE_SIZE)) { GeneratingPagingSource(++generation) }.flow,
      )
      val screen = mainScreen()
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

  private fun AndroidComposeUiTest<ComponentActivity>.mainScreen(): MainScreenObject {
    val activity = checkNotNull(activity)
    return MainScreenObject(
      provider = this,
      loadingDescription = activity.getString(R.string.characters_loading),
      retryText = activity.getString(R.string.characters_retry),
    )
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

private class MainScreenObject(
  private val provider: SemanticsNodeInteractionsProvider,
  loadingDescription: String,
  retryText: String,
) {
  val loadingIndicator = KNode(provider) {
    hasContentDescription(loadingDescription)
  }
  val retryButton = KNode(provider) {
    hasText(retryText)
  }

  fun character(name: String): KNode {
    return KNode(provider) {
      hasText(name)
    }
  }

  fun text(value: String): KNode {
    return KNode(provider) {
      hasText(value)
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
