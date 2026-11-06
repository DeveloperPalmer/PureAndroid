package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.kaspersky.components.composesupport.core.KNode
import io.github.kakaocup.compose.node.element.ComposeScreen
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.feature.main.domain.entity.Character

class CharacterDetailsScreen(
  semanticsProvider: SemanticsNodeInteractionsProvider,
) : ComposeScreen<CharacterDetailsScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag(CharacterDetailsTestTags.SCREEN) },
  ) {
  val backButton: KNode = child {
    hasText(R.string.character_details_back)
    hasClickAction()
  }

  val retryButton: KNode = child {
    hasText(R.string.characters_retry)
    hasClickAction()
  }

  val loadingIndicator: KNode = child {
    addSemanticsMatcher(
      SemanticsMatcher.expectValue(
        SemanticsProperties.ProgressBarRangeInfo,
        ProgressBarRangeInfo.Indeterminate,
      ),
    )
  }

  val error: KNode = child {
    hasTestTag(CharacterDetailsTestTags.DETAILS_ERROR)
  }

  val name: KNode = child {
    hasTestTag(CharacterDetailsTestTags.DETAILS_NAME)
  }

  val species: KNode = child {
    hasTestTag(CharacterDetailsTestTags.DETAILS_SPECIES)
  }

  val origin: KNode = child {
    hasTestTag(CharacterDetailsTestTags.DETAILS_ORIGIN)
  }

  val location: KNode = child {
    hasTestTag(CharacterDetailsTestTags.DETAILS_LOCATION)
  }

  fun assertLoading() {
    loadingIndicator.assertIsDisplayed()
    error.assertDoesNotExist()
    retryButton.assertDoesNotExist()
    assertNoCharacter()
    assertBackAvailable()
  }

  fun assertCharacter(character: Character) {
    name.assertIsDisplayed()
    name.assertTextEquals(character.name)
    species.assertIsDisplayed()
    species.assertTextEquals(character.species)
    origin.assertIsDisplayed()
    origin.assertTextEquals(character.origin.name)
    location.assertIsDisplayed()
    location.assertTextEquals(character.location.name)
    loadingIndicator.assertDoesNotExist()
    error.assertDoesNotExist()
    retryButton.assertDoesNotExist()
    assertBackAvailable()
  }

  fun assertError(
    @StringRes message: Int
  ) {
    error.assertIsDisplayed()
    error.assertTextEquals(message)
    retryButton.assertIsDisplayed()
    retryButton.assertIsEnabled()
    loadingIndicator.assertDoesNotExist()
    assertNoCharacter()
    assertBackAvailable()
  }

  fun assertBackAvailable() {
    backButton.assertIsDisplayed()
    backButton.assertIsEnabled()
  }

  private fun assertNoCharacter() {
    name.assertDoesNotExist()
    species.assertDoesNotExist()
    origin.assertDoesNotExist()
    location.assertDoesNotExist()
  }
}
