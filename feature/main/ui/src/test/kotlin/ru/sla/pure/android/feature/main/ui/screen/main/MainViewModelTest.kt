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
import org.junit.jupiter.api.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
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
  fun `начальное состояние - контент в загрузке, refresh не активен`() = runTest(testDispatcher) {
    val viewModel = MainViewModel(mainModel)

    viewModel.testWithInternalState(this) {
      val initial = containerHost.container.stateFlow.value

      assertEquals(ContentLoadState.Loading, initial.contentLoadState)
      assertFalse(initial.refreshInProgress)

      cancelAndIgnoreRemainingItems()
    }
  }

  @Test
  fun `состояние загрузки маппится в индикатор контента`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates(refresh = LoadState.Loading))

    assertEquals(ContentLoadState.Loading, state.contentLoadState)
  }

  @Test
  fun `активный refresh поверх контента включает refreshInProgress`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(refresh = LoadState.Loading),
      itemsCount = 1,
    )

    assertTrue(state.refreshInProgress)
  }

  @Test
  fun `первичная загрузка без данных не включает refreshInProgress`() = runTest(testDispatcher) {
    // Иначе при пустом экране индикатор потягивания дублирует полноэкранный спиннер
    val state = stateAfter(loadStates(refresh = LoadState.Loading))

    assertFalse(state.refreshInProgress)
  }

  @Test
  fun `догрузка страницы не включает refreshInProgress`() = runTest(testDispatcher) {
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
  fun `успешная загрузка переводит контент в готовность`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates())

    assertEquals(ContentLoadState.Ready, state.contentLoadState)
    assertFalse(state.refreshInProgress)
  }

  @Test
  fun `ошибка обновления показывает ошибку контента`() = runTest(testDispatcher) {
    val state = stateAfter(loadStates(refresh = LoadState.Error(ConnectivityError.NoConnection(null))))

    val error = state.contentLoadState as ContentLoadState.Error
    assertEquals(R.string.error_connection, error.error.messageResource)
    assertFalse(state.refreshInProgress)
  }

  @Test
  fun `таймаут показывается как ошибка соединения`() = runTest(testDispatcher) {
    val cause = ConnectivityError.TimeOut(SocketTimeoutException())
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_connection, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  fun `SSL ошибка показывается как ошибка соединения`() = runTest(testDispatcher) {
    val cause = ConnectivityError.SSLError(SSLException("handshake failed"))
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_connection, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  fun `ошибка API показывается как ошибка сервера`() = runTest(testDispatcher) {
    val cause = ApiError(500, null, "Internal server error")
    val state = stateAfter(loadStates(refresh = LoadState.Error(cause)))
    val error = state.contentLoadState as ContentLoadState.Error

    assertEquals(R.string.error_server, error.error.messageResource)
    assertSame(cause, error.error.cause)
  }

  @Test
  fun `ошибка догрузки страницы тоже показывает ошибку контента`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(append = LoadState.Error(ConnectivityError.NoConnection(null)))
    )

    assertTrue(state.contentLoadState is ContentLoadState.Error)
  }

  @Test
  fun `загрузка важнее ошибки при конфликте состояний`() = runTest(testDispatcher) {
    val state = stateAfter(
      loadStates(
        refresh = LoadState.Loading,
        append = LoadState.Error(ConnectivityError.NoConnection(null)),
      )
    )

    assertEquals(ContentLoadState.Loading, state.contentLoadState)
  }

  @Test
  fun `поток персонажей пробрасывается из модели без изменений`() = runTest(testDispatcher) {
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
    val viewModel = MainViewModel(mainModel)
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
