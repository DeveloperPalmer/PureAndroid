package ru.sla.pure.android.feature.main.ui.screen.main

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage

class MainViewModelTest {
    @Test
    fun `передаёт экрану поток пагинации модели`() = runTest {
        val repository = object : CharacterRepository {
            override suspend fun readPage(page: Int): CharacterPage = error("UI is not collecting yet")
            override suspend fun readCharacter(id: Int): Character = error("Unused")
        }
        val model = MainModel(backgroundScope, repository)
        MainViewModel(model).testWithInternalState(this) {
            runOnCreate()
            expectInternalState { copy(characters = model.characters) }
        }
    }
}
