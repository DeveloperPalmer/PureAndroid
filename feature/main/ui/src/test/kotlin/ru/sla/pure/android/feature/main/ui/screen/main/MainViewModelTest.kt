package ru.sla.pure.android.feature.main.ui.screen.main

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

class MainViewModelTest {
    @Test
    fun `загружает первую страницу при создании`() = runTest {
        val repository = FakeRepository()
        val first = page(1, listOf(character(1)), 2)
        repository.response = { first }
        MainViewModel(MainModel(repository)).testWithInternalState(this) {
            runOnCreate()
            expectInternalState { copy(characters = first.characters, isLoading = false, nextPage = 2) }
            assertEquals(listOf(1), repository.calls)
        }
    }

    @Test
    fun `добавляет страницы без дубликатов и останавливается на последней`() = runTest {
        val repository = FakeRepository()
        val first = page(1, listOf(character(1)), 2)
        val second = page(2, listOf(character(1), character(2)), null)
        repository.response = { if (it == 1) first else second }
        val viewModel = MainViewModel(MainModel(repository))
        viewModel.testWithInternalState(this) {
            runOnCreate()
            expectInternalState { copy(characters = first.characters, isLoading = false, nextPage = 2) }
            viewModel.loadMore().join()
            expectInternalState { copy(isLoading = true) }
            expectInternalState { copy(characters = second.characters, isLoading = false, nextPage = null) }
            viewModel.loadMore().join()
            assertEquals(listOf(1, 2), repository.calls)
        }
    }

    @Test
    fun `ошибка подгрузки сохраняет список а повтор запрашивает ту же страницу`() = runTest {
        val repository = FakeRepository()
        val first = page(1, listOf(character(1)), 2)
        repository.response = { first }
        val viewModel = MainViewModel(MainModel(repository))
        viewModel.testWithInternalState(this) {
            runOnCreate()
            expectInternalState { copy(characters = first.characters, isLoading = false, nextPage = 2) }
            repository.response = { throw ServerException.Unreachable(null) }
            viewModel.loadMore().join()
            expectInternalState { copy(isLoading = true) }
            expectInternalState { copy(isLoading = false, error = LoadError.Connection, failedPage = 2) }
            repository.response = { page(2, listOf(character(2)), null) }
            viewModel.retry().join()
            expectInternalState { copy(isLoading = true, error = null, failedPage = null) }
            expectInternalState { copy(characters = listOf(character(1), character(2)), isLoading = false, nextPage = null) }
            assertEquals(listOf(1, 2, 2), repository.calls)
        }
    }

    @Test
    fun `обновление заменяет список и сбрасывает пагинацию`() = runTest {
        val repository = FakeRepository()
        repository.response = { page(1, listOf(character(1)), null) }
        val viewModel = MainViewModel(MainModel(repository))
        viewModel.testWithInternalState(this) {
            runOnCreate()
            expectInternalState { copy(characters = listOf(character(1)), isLoading = false) }
            repository.response = { page(1, listOf(character(3)), 2) }
            viewModel.refresh().join()
            expectInternalState { copy(isLoading = true) }
            expectInternalState { copy(characters = listOf(character(3)), isLoading = false, nextPage = 2) }
        }
    }

    @Test
    fun `параллельные нажатия не запускают повторные запросы`() = runTest {
        val repository = FakeRepository()
        val gate = CompletableDeferred<CharacterPage>()
        repository.response = { gate.await() }
        val viewModel = MainViewModel(MainModel(repository))
        viewModel.testWithInternalState(this) {
            runOnCreate()
            testScheduler.runCurrent()
            viewModel.refresh().join()
            assertEquals(listOf(1), repository.calls)
            gate.complete(page(1, emptyList(), null))
            expectInternalState { copy(isLoading = false) }
        }
    }

    private class FakeRepository : CharacterRepository {
        val calls = mutableListOf<Int>()
        var response: suspend (Int) -> CharacterPage = { error("No response") }
        override suspend fun readPage(page: Int): CharacterPage {
            calls += page
            return response(page)
        }
        override suspend fun readCharacter(id: Int): Character = error("Unused")
    }
}

private fun page(number: Int, characters: List<Character>, next: Int?) =
    CharacterPage(characters, number, 40, 2, next, if (number > 1) number - 1 else null)

private fun character(id: Int) = Character(
    id, "Character $id", CharacterStatus.Alive, "Human", "", CharacterGender.Unknown,
    CharacterLocation("Earth", ""), CharacterLocation("Citadel", ""), "",
)
