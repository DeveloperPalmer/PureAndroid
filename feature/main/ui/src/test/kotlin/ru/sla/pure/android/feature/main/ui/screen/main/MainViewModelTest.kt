package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.ui.routing.FlowEvent
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
  private val testDispatcher = StandardTestDispatcher()
  private val mainModel = mockk<MainModel>()

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    every { mainModel.characters } returns flowOf(PagingData.empty())
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  @DisplayName("В начальном состоянии контент загружается, индикатор обновления выключен")
  fun `initial state shows loading without refresh`() = runTest(testDispatcher) {
    val viewModel = MainViewModel(mainModel) {}

    viewModel.testWithInternalState(this) {
      val initial = containerHost.container.stateFlow.value

      assertEquals(ContentLoadState.Loading, initial.contentLoadState)
      assertFalse(initial.refreshInProgress)

      cancelAndIgnoreRemainingItems()
    }
  }

  @Test
  @DisplayName("Нажатие на персонажа отправляет событие флоу с его ID")
  fun `opening a character emits a flow event with its ID`() = runTest(testDispatcher) {
    val events = mutableListOf<Event>()
    val viewModel = MainViewModel(mainModel) { events.add(it) }

    viewModel.onOpenCharacter(42).join()

    assertEquals(listOf(FlowEvent.CharacterDetailsRequested(42)), events)
  }

  @Test
  @DisplayName("Состояние загрузки маппится в индикатор контента")
  fun `loading state shows the content indicator`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates(refresh = LoadState.Loading))

    assertEquals(ContentLoadState.Loading, state.contentLoadState)
  }

  @Test
  @DisplayName("Активный refresh поверх контента включает refreshInProgress")
  fun `refresh with content sets refresh in progress`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(refresh = LoadState.Loading),
      itemsCount = 1,
    )

    assertTrue(state.refreshInProgress)
  }

  @Test
  @DisplayName("Первичная загрузка без данных не включает refreshInProgress")
  fun `initial load without items does not set refresh in progress`() = runTest(testDispatcher) {
    // Иначе при пустом экране индикатор потягивания дублирует полноэкранный спиннер
    val state = stateAfter(loadStates(refresh = LoadState.Loading))

    assertFalse(state.refreshInProgress)
  }

  @Test
  @DisplayName("Догрузка страницы не включает refreshInProgress")
  fun `append does not set refresh in progress`() = runTest(testDispatcher) {
    // Сначала готовность: догрузка возможна только когда первая страница уже загружена
    val state = stateAfter(
      loadStates(),
      loadStates(append = LoadState.Loading),
    )

    assertFalse(state.refreshInProgress)
    // Текущее поведение asLceState: загрузка append тоже даёт индикатор контента
    assertEquals(ContentLoadState.Loading, state.contentLoadState)
  }

  @Test
  @DisplayName("Успешная загрузка переводит контент в готовность")
  fun `successful load marks content as ready`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates())

    assertEquals(ContentLoadState.Ready, state.contentLoadState)
    assertFalse(state.refreshInProgress)
  }

  @Test
  @DisplayName("Ошибка обновления показывает ошибку контента")
  fun `refresh failure shows a content error`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates(refresh = LoadState.Error(ConnectivityError.NoConnection(null))))

    val error = state.contentLoadState as ContentLoadState.Error
    assertEquals(R.string.error_connection, error.error.messageResource)
    assertFalse(state.refreshInProgress)
  }

  @Test
  @DisplayName("Таймаут показывается как ошибка соединения")
  fun `timeout shows a connection error`() = runTest(testDispatcher) {
    val cause = ConnectivityError.TimeOut(SocketTimeoutException())
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_connection, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  @DisplayName("Ошибка SSL показывается как ошибка соединения")
  fun `SSL failure shows a connection error`() = runTest(testDispatcher) {
    val cause = ConnectivityError.SSLError(SSLException("handshake failed"))
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_connection, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  @DisplayName("Ошибка API показывается как ошибка сервера")
  fun `API error shows a server error`() = runTest(testDispatcher) {
    val cause = ApiError(500, null, "Internal server error")
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_server, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  @DisplayName("Ошибка догрузки страницы тоже показывает ошибку контента")
  fun `append failure shows a content error`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(append = LoadState.Error(ConnectivityError.NoConnection(null)))
    )

    assertTrue(state.contentLoadState is ContentLoadState.Error)
  }

  @Test
  @DisplayName("Загрузка важнее ошибки при конфликте состояний")
  fun `loading takes precedence over error`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(
        refresh = LoadState.Loading,
        append = LoadState.Error(ConnectivityError.NoConnection(null)),
      )
    )

    assertEquals(ContentLoadState.Loading, state.contentLoadState)
  }

  @Test
  @DisplayName("Поток персонажей пробрасывается из модели без изменений")
  fun `characters flow is passed through from the model`() = runTest(testDispatcher) {
    val characters = flowOf(PagingData.empty<Character>())
    every { mainModel.characters } returns characters

    val state = stateAfter()

    assertSame(characters, state.characters)
  }

  private fun loadStates(
    refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = false),
    prepend: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
    append: LoadState = LoadState.NotLoading(endOfPaginationReached = false),
  ) = CombinedLoadStates(
    refresh = refresh,
    prepend = prepend,
    append = append,
    source = LoadStates(refresh, prepend, append),
  )

  // orbit-test прогоняет интенты синхронно: runOnCreate() отыгрывает onCreate контейнера,
  // каждое изменение состояния читаем через awaitInternalState() — ровно по одному на действие,
  // внешний container.stateFlow.value для этого не подходит: он обновляется асинхронно
  // и может отставать на шаг.
  private suspend fun TestScope.stateAfter(
    vararg changes: CombinedLoadStates,
    itemsCount: Int = 0,
  ): ViewState {
    val viewModel = MainViewModel(mainModel) {}
    lateinit var state: ViewState
    viewModel.testWithInternalState(this) {
      runOnCreate()
      state = awaitInternalState() // состояние после onCreate: поток персонажей из модели
      if (itemsCount > 0) {
        containerHost.onCharactersCountChanged(itemsCount)
        state = awaitInternalState()
      }
      changes.forEach { change ->
        containerHost.onLoadStatesChanged(change)
        state = awaitInternalState()
      }
      cancelAndIgnoreRemainingItems()
    }
    return state
  }
}
