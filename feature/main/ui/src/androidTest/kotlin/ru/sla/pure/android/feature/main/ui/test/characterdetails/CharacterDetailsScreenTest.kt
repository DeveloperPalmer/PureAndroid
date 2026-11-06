package ru.sla.pure.android.feature.main.ui.test.characterdetails

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.lifecycle.viewmodel.compose.viewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsViewModel
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.detailsCharacter
import ru.sla.pure.android.uikit.theme.AppTheme
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(ExperimentalTestApi::class)
class CharacterDetailsScreenTest : BaseTestCase() {
  private val repository = mockk<CharacterRepository>()
  private val modelScope = createCoroutineScope()
  private val navigationEvents = CopyOnWriteArrayList<Event>()

  @AfterEach
  fun cancelModelScope() {
    modelScope.cancel()
  }

  @Test
  @DisplayName("Открытие деталей загружает выбранного персонажа и убирает индикатор")
  fun openingLoadsRequestedCharacter() = runScreenTest { steps ->
    val response = CompletableDeferred<Character>()
    coEvery { repository.readCharacter(detailsCharacter.id) } coAnswers { response.await() }
    val screen = showScreen()

    steps.step("Запрошен выбранный персонаж, до ответа отображается загрузка") {
      coVerify(timeout = 5_000) { repository.readCharacter(detailsCharacter.id) }
      assertRequests(1)
      screen.assertLoading()
    }
    steps.step("Ответ репозитория заменяет индикатор всеми данными персонажа") {
      response.complete(detailsCharacter)
      screen.assertCharacter(detailsCharacter)
      assertRequests(1)
      assertEquals(emptyList<Event>(), navigationEvents)
    }
  }

  @Test
  @DisplayName("Мгновенный ответ репозитория не теряется при открытии экрана")
  fun immediateResponseShowsCharacter() = runScreenTest { steps ->
    coEvery { repository.readCharacter(detailsCharacter.id) } returns detailsCharacter
    val screen = showScreen()

    steps.step("Данные показаны даже без задержки ответа") {
      screen.assertCharacter(detailsCharacter)
      assertRequests(1)
    }
  }

  @Test
  @DisplayName("Повторить после ошибки соединения загружает того же персонажа")
  fun retryAfterConnectionErrorLoadsSameCharacter() = runScreenTest { steps ->
    val firstResponse = CompletableDeferred<Character>()
    val retryResponse = CompletableDeferred<Character>()

    coEvery {
      repository.readCharacter(detailsCharacter.id)
    } coAnswers {
      firstResponse.await()
    } coAndThen {
      retryResponse.await()
    }

    val screen = showScreen()

    steps.step("Ошибка соединения заменяет загрузку и предлагает повторить") {
      screen.assertLoading()
      firstResponse.completeExceptionally(ConnectivityError.NoConnection(cause = null))
      screen.assertError(R.string.error_connection)
    }
    steps.step("Повторить отправляет один новый запрос с исходным id и убирает ошибку") {
      screen.retryButton.performClick()
      coVerify(timeout = 5_000, exactly = 2) { repository.readCharacter(detailsCharacter.id) }
      assertRequests(2)
      screen.assertLoading()
    }
    steps.step("Успешный повтор показывает данные и убирает кнопку повторить") {
      retryResponse.complete(detailsCharacter)
      screen.assertCharacter(detailsCharacter)
      assertRequests(2)
      assertEquals(emptyList<Event>(), navigationEvents)
    }
  }

  @Test
  @DisplayName("Ошибка сервера и ошибка повторного запроса показывают актуальное сообщение")
  fun failedRetryShowsNewErrorAndAllowsAnotherRetry() = runScreenTest { steps ->
    val firstResponse = CompletableDeferred<Character>()
    val retryResponse = CompletableDeferred<Character>()
    val finalResponse = CompletableDeferred<Character>()
    coEvery { repository.readCharacter(detailsCharacter.id) } coAnswers { firstResponse.await() } coAndThen {
      retryResponse.await()
    } coAndThen { finalResponse.await() }
    val screen = showScreen()

    steps.step("Ошибка сервера отображается с возможностью повтора") {
      screen.assertLoading()
      firstResponse.completeExceptionally(ApiError(status = 503, code = null, description = null))
      screen.assertError(R.string.error_server)
    }
    steps.step("Ошибка следующего запроса заменяет предыдущее сообщение") {
      screen.retryButton.performClick()
      screen.assertLoading()
      retryResponse.completeExceptionally(IllegalStateException("Unexpected response"))
      screen.assertError(R.string.error_unexpected)
    }
    steps.step("После повторной ошибки можно снова загрузить данные") {
      screen.retryButton.performClick()
      screen.assertLoading()
      finalResponse.complete(detailsCharacter)
      screen.assertCharacter(detailsCharacter)
      assertRequests(3)
    }
  }

  @Test
  @DisplayName("Назад во время загрузки отправляет событие закрытия без повторного запроса")
  fun backDuringLoadingEmitsDismissal() = runScreenTest { steps ->
    val response = CompletableDeferred<Character>()
    coEvery { repository.readCharacter(detailsCharacter.id) } coAnswers { response.await() }
    val screen = showScreen()

    steps.step("Назад доступно, пока ответ репозитория ещё не получен") {
      coVerify(timeout = 5_000) { repository.readCharacter(detailsCharacter.id) }
      screen.assertLoading()
      screen.backButton.performClick()
    }
    steps.step("ViewModel отправляет ровно одно событие закрытия деталей") {
      waitUntil(timeoutMillis = 5_000) { navigationEvents.isNotEmpty() }
      assertEquals(listOf(MainFlowEvent.CharacterDetailsDismissed), navigationEvents)
      assertRequests(1)
    }
  }

  private fun assertRequests(count: Int) {
    coVerify(exactly = count) { repository.readCharacter(detailsCharacter.id) }
    coVerify(exactly = count) { repository.readCharacter(any()) }
    coVerify(exactly = 0) { repository.readPage(any()) }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.showScreen(): CharacterDetailsScreen {
    val model = MainModel(modelScope, repository)
    setContent {
      AppTheme {
        // Activity owns the ViewModelStore, so Compose test teardown also clears the ViewModel.
        val viewModel = viewModel {
          CharacterDetailsViewModel(
            characterId = detailsCharacter.id,
            mainModel = model,
            flowEventSink = FlowEventSink { navigationEvents.add(it) },
          )
        }
        CharacterDetailsScreen(viewModel)
      }
    }
    return CharacterDetailsScreen(this)
  }
}
