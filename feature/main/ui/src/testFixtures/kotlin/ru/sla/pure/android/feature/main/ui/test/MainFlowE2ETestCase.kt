package ru.sla.pure.android.feature.main.ui.test

import androidx.compose.ui.test.ExperimentalTestApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.testCharacter
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import ru.sla.pure.android.testing.BaseAppTestCase

@OptIn(ExperimentalTestApi::class)
// Only the concrete subclass in app/androidTest is an instrumentation entry point.
@Suppress("AbstractClassCanBeConcreteClass")
abstract class MainFlowE2ETestCase : BaseAppTestCase() {
  @Test
  @DisplayName("Список, детали и возврат работают через настоящий application graph")
  fun listDetailsAndBackUseProductionFlow() = runAppTest {
    backend.enqueueJson(CharacterHttpFixtures.page(listOf(testCharacter)))
    backend.enqueueJson(CharacterHttpFixtures.details(testCharacter))
    val mainScreen = MainScreen(this)
    val detailsScreen = CharacterDetailsScreen(this)

    mainScreen.character(testCharacter.name).assertIsDisplayed()
    assertEquals("/character?page=1", backend.takeRequestTarget())

    mainScreen.character(testCharacter.name).performClick()
    detailsScreen.assertCharacter(testCharacter)
    assertEquals("/character/${testCharacter.id}", backend.takeRequestTarget())

    detailsScreen.backButton.performClick()
    mainScreen.character(testCharacter.name).assertIsDisplayed()
    assertEquals(2, backend.requestCount)
  }

  @Test
  @DisplayName("Ошибка сервера в деталях и повтор проходят через настоящий HTTP stack")
  fun detailsServerErrorAndRetryUseProductionFlow() = runAppTest {
    backend.enqueueJson(CharacterHttpFixtures.page(listOf(testCharacter)))
    backend.enqueueJson(body = "{\"error\":\"Unavailable\"}", code = 503)
    backend.enqueueJson(CharacterHttpFixtures.details(testCharacter))
    val mainScreen = MainScreen(this)
    val detailsScreen = CharacterDetailsScreen(this)

    mainScreen.character(testCharacter.name).assertIsDisplayed()
    assertEquals("/character?page=1", backend.takeRequestTarget())
    mainScreen.character(testCharacter.name).performClick()

    detailsScreen.assertError(R.string.error_server)
    assertEquals("/character/${testCharacter.id}", backend.takeRequestTarget())

    detailsScreen.retryButton.performClick()
    detailsScreen.assertCharacter(testCharacter)
    assertEquals("/character/${testCharacter.id}", backend.takeRequestTarget())
    assertEquals(3, backend.requestCount)
  }
}
