package ru.sla.pure.android.testing.feature.main

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.lifecycle.Lifecycle
import org.junit.jupiter.api.Test
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.testCharacter
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import ru.sla.pure.android.feature.main.ui.test.CharacterHttpFixtures
import ru.sla.pure.android.feature.main.ui.test.MainFlowE2ETestCase

@OptIn(ExperimentalTestApi::class)
class MainFlowE2ETest : MainFlowE2ETestCase() {
  @Test
  fun backFromMainRootLeavesActivity() = runAppTest {
    backend.enqueueJson(CharacterHttpFixtures.page(listOf(testCharacter)))
    MainScreen(this).character(testCharacter.name).assertIsDisplayed()

    val mainActivity = requireNotNull(activity)
    runOnUiThread { mainActivity.onBackPressedDispatcher.onBackPressed() }

    waitUntil(timeoutMillis = 5_000) {
      runOnUiThread { !mainActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
    }
  }
}
