package ru.sla.pure.android.feature.main.ui.test.characterdetails

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
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
import io.mockk.mockk
import kotlinx.coroutines.cancel
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.testing.BaseTestCase
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsViewModel
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import ru.sla.pure.android.feature.main.ui.screen.main.MainViewModel
import ru.sla.pure.android.uikit.theme.AppTheme
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
class CharacterDetailsStressTest : BaseTestCase() {
  @Test
  fun opensCharacterDetailsRepeatedly() {
    val arguments = InstrumentationRegistry.getArguments()
    val iterations = requireNotNull((arguments.getString("detailsIterations") ?: "10").toIntOrNull()) {
      "detailsIterations must be a positive integer"
    }
    require(iterations > 0) { "detailsIterations must be a positive integer" }

    val characters = listOf(testCharacter(1), testCharacter(2))
    val scope = createCoroutineScope()
    val repository = mockk<CharacterRepository>()
    coEvery { repository.readPage(1) } returns CharacterPage(characters, 1, characters.size, 1, null, null)
    coEvery { repository.readCharacter(any()) } answers { characters.single { it.id == firstArg<Int>() } }
    val model = MainModel(scope, repository)
    val selectedId = mutableStateOf<Int?>(null)
    val eventSink = FlowEventSink { event ->
      when (event) {
        is MainFlowEvent.CharacterDetailsRequested -> selectedId.value = event.characterId
        MainFlowEvent.CharacterDetailsDismissed -> selectedId.value = null
      }
    }

    try {
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
    } finally {
      scope.cancel()
    }
  }

  private fun testCharacter(id: Int) = Character(
    id = id,
    name = "Character $id",
    status = CharacterStatus.Alive,
    species = "Human",
    type = "",
    gender = CharacterGender.Unknown,
    origin = CharacterLocation("Origin $id", ""),
    location = CharacterLocation("Location $id", ""),
    imageUrl = "",
  )
}
