package ru.sla.pure.android.feature.main.ui.test.characterdetails

import androidx.activity.ComponentActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.entity.UiError
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsContent
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsViewModel
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.ViewState
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.testCharacter
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import ru.sla.pure.android.feature.main.ui.screen.main.MainViewModel
import ru.sla.pure.android.uikit.theme.AppTheme
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
class CharacterDetailsScreenTest : BaseTestCase() {
  private val repository by lazy { mockk<CharacterRepository>() }
  private val modelScope = createCoroutineScope()
  private val navigationEvents = CopyOnWriteArrayList<Event>()

  @AfterEach
  fun cancelModelScope() {
    modelScope.cancel()
  }

  @Test
  @DisplayName("Загрузка скрывает ранее загруженные данные и оставляет кнопку назад")
  fun loadingHidesPreviousCharacter() = runScreenTest { steps ->
    val screen = showContent(ViewState(ContentLoadState.Loading, testCharacter))

    steps.step("Отображается только загрузка и доступна кнопка назад") {
      screen.assertLoading()
    }
  }

  @Test
  @DisplayName("Ready без персонажа показывает загрузку до получения данных")
  fun readyWithoutCharacterShowsLoading() = runScreenTest { steps ->
    val screen = showContent(ViewState(ContentLoadState.Ready))

    steps.step("Промежуточное состояние не оставляет пустой экран") {
      screen.assertLoading()
    }
  }

  @Test
  @DisplayName("Успешная загрузка отображает имя, вид, происхождение и текущее место")
  fun readyShowsEveryCharacterField() = runScreenTest { steps ->
    val screen = showContent(ViewState(ContentLoadState.Ready, testCharacter))

    steps.step("Все поля показаны на своих местах, ошибка и загрузка отсутствуют") {
      screen.assertCharacter(testCharacter)
    }
  }

  @Test
  @DisplayName("Одинаковые названия происхождения и текущего места показаны в двух полях")
  fun equalOriginAndLocationRemainSeparateFields() = runScreenTest { steps ->
    val character = testCharacter.copy(location = testCharacter.origin)
    val screen = showContent(ViewState(ContentLoadState.Ready, character))

    steps.step("Оба поля присутствуют, даже если их значения совпадают") {
      screen.assertCharacter(character)
    }
  }

  @Test
  @DisplayName("Ошибка скрывает старые данные, нажатие повторить вызывает один запрос")
  fun errorHidesPreviousCharacterAndRetryCallsCallbackOnce() = runScreenTest { steps ->
    var retryCalls = 0
    var backCalls = 0
    val screen = showContent(
      state = ViewState(ContentLoadState.Error(UiError(R.string.error_connection)), testCharacter),
      onRetry = { retryCalls++ },
      onNavigateBack = { backCalls++ },
    )
    steps.step("Показаны локализованная ошибка и активная кнопка повторить") {
      screen.assertError(R.string.error_connection)
      runOnIdle { assertEquals(0, retryCalls) }
    }
    steps.step("Одно нажатие вызывает только повторную загрузку") {
      screen.retryButton.performClick()
      runOnIdle {
        assertEquals(1, retryCalls)
        assertEquals(0, backCalls)
      }
    }
  }

  @Test
  @DisplayName("Кнопка назад вызывает навигацию во всех состояниях экрана")
  fun backWorksInEveryLoadState() = runScreenTest { steps ->
    val state = mutableStateOf(ViewState())
    var backCalls = 0
    var retryCalls = 0
    setContent {
      AppTheme {
        CharacterDetailsContent(
          state = state.value,
          onNavigateBack = { backCalls++ },
          onRetry = { retryCalls++ },
        )
      }
    }
    val screen = CharacterDetailsScreen(this@runScreenTest)

    val states = listOf(
      "загрузка" to ViewState(),
      "ошибка" to ViewState(ContentLoadState.Error(UiError(R.string.error_server))),
      "готово без данных" to ViewState(ContentLoadState.Ready),
      "персонаж загружен" to ViewState(ContentLoadState.Ready, testCharacter),
    )
    states.forEachIndexed { index, (description, nextState) ->
      steps.step("Назад работает в состоянии: $description") {
        runOnIdle { state.value = nextState }
        screen.assertBackAvailable()
        screen.backButton.performClick()
        runOnIdle {
          assertEquals(index + 1, backCalls)
          assertEquals(0, retryCalls)
        }
      }
    }
  }

  @Test
  @DisplayName("Открытие деталей загружает выбранного персонажа и убирает индикатор")
  fun openingLoadsRequestedCharacter() = runScreenTest { steps ->
    val response = CompletableDeferred<Character>()
    coEvery { repository.readCharacter(testCharacter.id) } coAnswers { response.await() }
    val screen = showScreen()

    steps.step("Запрошен выбранный персонаж, до ответа отображается загрузка") {
      coVerify(timeout = 5_000) { repository.readCharacter(testCharacter.id) }
      assertRequests(1)
      screen.assertLoading()
    }
    steps.step("Ответ репозитория заменяет индикатор всеми данными персонажа") {
      response.complete(testCharacter)
      screen.assertCharacter(testCharacter)
      assertRequests(1)
      assertEquals(emptyList<Event>(), navigationEvents)
    }
  }

  @Test
  @DisplayName("Мгновенный ответ репозитория не теряется при открытии экрана")
  fun immediateResponseShowsCharacter() = runScreenTest { steps ->
    coEvery { repository.readCharacter(testCharacter.id) } returns testCharacter
    val screen = showScreen()

    steps.step("Данные показаны даже без задержки ответа") {
      screen.assertCharacter(testCharacter)
      assertRequests(1)
    }
  }

  @Test
  @DisplayName("Повторить после ошибки соединения загружает того же персонажа")
  fun retryAfterConnectionErrorLoadsSameCharacter() = runScreenTest { steps ->
    val firstResponse = CompletableDeferred<Character>()
    val retryResponse = CompletableDeferred<Character>()

    coEvery {
      repository.readCharacter(testCharacter.id)
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
      coVerify(timeout = 5_000, exactly = 2) { repository.readCharacter(testCharacter.id) }
      assertRequests(2)
      screen.assertLoading()
    }
    steps.step("Успешный повтор показывает данные и убирает кнопку повторить") {
      retryResponse.complete(testCharacter)
      screen.assertCharacter(testCharacter)
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
    coEvery { repository.readCharacter(testCharacter.id) } coAnswers { firstResponse.await() } coAndThen {
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
      finalResponse.complete(testCharacter)
      screen.assertCharacter(testCharacter)
      assertRequests(3)
    }
  }

  @Test
  @DisplayName("Назад во время загрузки отправляет событие закрытия без повторного запроса")
  fun backDuringLoadingEmitsDismissal() = runScreenTest { steps ->
    val response = CompletableDeferred<Character>()
    coEvery { repository.readCharacter(testCharacter.id) } coAnswers { response.await() }
    val screen = showScreen()

    steps.step("Назад доступно, пока ответ репозитория ещё не получен") {
      coVerify(timeout = 5_000) { repository.readCharacter(testCharacter.id) }
      screen.assertLoading()
      screen.backButton.performClick()
    }
    steps.step("ViewModel отправляет ровно одно событие закрытия деталей") {
      waitUntil(timeoutMillis = 5_000) { navigationEvents.isNotEmpty() }
      assertEquals(listOf(MainFlowEvent.CharacterDetailsDismissed), navigationEvents)
      assertRequests(1)
    }
  }

  @Test
  @DisplayName("Детали выбранного персонажа загружаются при каждом открытии")
  fun opensCharacterDetailsRepeatedly() {
    val arguments = InstrumentationRegistry.getArguments()
    val iterations = requireNotNull((arguments.getString("detailsIterations") ?: "10").toIntOrNull()) {
      "detailsIterations must be a positive integer"
    }
    require(iterations > 0) { "detailsIterations must be a positive integer" }

    val characters = listOf(testCharacter(1), testCharacter(2))
    coEvery { repository.readPage(1) } returns CharacterPage(characters, 1, characters.size, 1, null, null)
    coEvery { repository.readCharacter(any()) } answers { characters.single { it.id == firstArg<Int>() } }
    val model = MainModel(modelScope, repository)
    val selectedId = mutableStateOf<Int?>(null)
    val eventSink = FlowEventSink { event ->
      when (event) {
        is MainFlowEvent.CharacterDetailsRequested -> selectedId.value = event.characterId
        MainFlowEvent.CharacterDetailsDismissed -> selectedId.value = null
      }
    }

    runScreenTest(testTimeout = (15L * iterations + 30).seconds) {
      val backLabel = checkNotNull(activity).getString(R.string.character_details_back)
      setContent {
        AppTheme {
          val listViewModel = viewModel { MainViewModel(model, eventSink) }
          val characterId = selectedId.value
          if (characterId == null) {
            MainScreen(listViewModel)
          } else {
            val owner = remember(characterId) {
              object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
              }
            }
            DisposableEffect(owner) {
              onDispose { owner.viewModelStore.clear() }
            }
            val detailsViewModel = viewModel(viewModelStoreOwner = owner) {
              CharacterDetailsViewModel(characterId, model, eventSink)
            }
            CharacterDetailsScreen(detailsViewModel)
          }
        }
      }

      val timeouts = mutableListOf<String>()
      val characterList = hasTestTag("characters")
      val loadingIndicator = expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate)
      repeat(iterations) { index ->
        val character = characters[index % characters.size]
        waitUntilExactlyOneExists(characterList, timeoutMillis = 5_000)
        onNodeWithText(character.name).performClick()
        waitUntilExactlyOneExists(hasText(backLabel), timeoutMillis = 5_000)
        try {
          // Место происхождения отображается только в деталях и отличается у каждого персонажа.
          waitUntilExactlyOneExists(hasText(character.origin.name), timeoutMillis = 3_000)
        } catch (_: ComposeTimeoutException) {
          val stillLoading = onAllNodes(loadingIndicator).fetchSemanticsNodes().isNotEmpty()
          timeouts += "#${index + 1} (${character.name}, loading=$stillLoading)"
        }
        onNodeWithText(backLabel).performClick()
      }
      waitUntilExactlyOneExists(characterList, timeoutMillis = 5_000)
      assertTrue(timeouts.isEmpty(), "${timeouts.size}/$iterations details timeouts: ${timeouts.joinToString()}")
    }
  }

  private fun AndroidComposeUiTest<ComponentActivity>.showContent(
    state: ViewState,
    onNavigateBack: () -> Unit = {},
    onRetry: () -> Unit = {},
  ): CharacterDetailsScreen {
    setContent {
      AppTheme {
        CharacterDetailsContent(
          state = state,
          onNavigateBack = onNavigateBack,
          onRetry = onRetry
        )
      }
    }
    return CharacterDetailsScreen(this)
  }

  private fun assertRequests(count: Int) {
    coVerify(exactly = count) { repository.readCharacter(testCharacter.id) }
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
            characterId = testCharacter.id,
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
