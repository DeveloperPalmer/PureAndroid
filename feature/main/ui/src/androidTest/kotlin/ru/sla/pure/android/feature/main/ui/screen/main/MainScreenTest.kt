package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.uikit.theme.AppTheme

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun loadingHasAccessibleDescription() {
        compose.setContent { AppTheme { MainContent(ViewState(), {}, {}, {}) } }
        compose.onNodeWithContentDescription("Загрузка персонажей").assertIsDisplayed()
    }

    @Test
    fun emptyPageOffersRefresh() {
        var refreshes = 0
        compose.setContent {
            AppTheme { MainContent(ViewState(isLoading = false), { refreshes++ }, {}, {}) }
        }
        compose.onNodeWithText("Персонажей пока нет").assertIsDisplayed()
        compose.onNodeWithText("Обновить").performClick()
        assertEquals(1, refreshes)
    }

    @Test
    fun paginationFailureKeepsCharactersAndOffersRetry() {
        var retries = 0
        compose.setContent {
            AppTheme {
                MainContent(
                    ViewState(listOf(rick), false, 2, 2, LoadError.Connection), {}, {}, { retries++ },
                )
            }
        }
        compose.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        compose.onNodeWithText("Повторить").performClick()
        assertEquals(1, retries)
    }

    @Test
    fun nextPageButtonRequestsMore() {
        var loads = 0
        compose.setContent {
            AppTheme { MainContent(ViewState(listOf(rick), false, 2), {}, { loads++ }, {}) }
        }
        compose.onNodeWithText("Загрузить ещё").performClick()
        assertEquals(1, loads)
    }
}

private val rick = Character(
    1, "Rick Sanchez", CharacterStatus.Alive, "Human", "", CharacterGender.Male,
    CharacterLocation("Earth", ""), CharacterLocation("Citadel", ""), "",
)
