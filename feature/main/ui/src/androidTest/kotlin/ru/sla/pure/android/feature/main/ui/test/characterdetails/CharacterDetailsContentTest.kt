package ru.sla.pure.android.feature.main.ui.test.characterdetails

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.entity.UiError
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsContent
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.ViewState
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.detailsCharacter
import ru.sla.pure.android.uikit.theme.AppTheme

@OptIn(ExperimentalTestApi::class)
class CharacterDetailsContentTest : BaseTestCase() {
  @Test
  @DisplayName("Загрузка скрывает ранее загруженные данные и оставляет кнопку назад")
  fun loadingHidesPreviousCharacter() = runScreenTest { steps ->
    val screen = showContent(ViewState(ContentLoadState.Loading, detailsCharacter))

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
    val screen = showContent(ViewState(ContentLoadState.Ready, detailsCharacter))

    steps.step("Все поля показаны на своих местах, ошибка и загрузка отсутствуют") {
      screen.assertCharacter(detailsCharacter)
    }
  }

  @Test
  @DisplayName("Одинаковые названия происхождения и текущего места показаны в двух полях")
  fun equalOriginAndLocationRemainSeparateFields() = runScreenTest { steps ->
    val character = detailsCharacter.copy(location = detailsCharacter.origin)
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
      state = ViewState(ContentLoadState.Error(UiError(R.string.error_connection)), detailsCharacter),
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
      "персонаж загружен" to ViewState(ContentLoadState.Ready, detailsCharacter),
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
}
