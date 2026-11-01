package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.entity.UiError
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage

class MainViewModelTest {
  @Test
  fun `передаёт экрану поток пагинации модели`() {
    runTest {
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

  @Test
  fun `преобразует ошибки пагинации в готовые UiError`() {
    runTest {
      val model = MainModel(backgroundScope, unusedRepository())
      val errors = listOf(
        ServerException.Unreachable(null) to R.string.error_connection,
        ServerException.Unauthorized(null) to R.string.error_server,
        IllegalStateException("Unexpected") to R.string.error_unexpected,
      )
      MainViewModel(model).testWithInternalState(this) {
        runOnCreate()
        expectInternalState { copy(characters = model.characters) }
        errors.forEach { (error, message) ->
          containerHost.onLoadStatesChanged(loadStates(refresh = LoadState.Error(error)))
          expectInternalState {
            copy(
              contentLoadState = ContentLoadState.Error(
                error = UiError(
                  messageResource = message,
                  cause = error,
                ),
              ),
            )
          }
        }
        containerHost.onLoadStatesChanged(loadStates(refresh = LoadState.Loading))
        expectInternalState { copy(contentLoadState = ContentLoadState.Loading, refreshInProgress = true) }
        containerHost.onLoadStatesChanged(loadStates())
        expectInternalState { copy(contentLoadState = ContentLoadState.Ready, refreshInProgress = false) }
      }
    }
  }

  @Test
  fun `обрабатывает ошибку следующей страницы и её повторную загрузку`() {
    runTest {
      val model = MainModel(backgroundScope, unusedRepository())
      val error = ServerException.Unreachable(null)
      MainViewModel(model).testWithInternalState(this) {
        runOnCreate()
        expectInternalState { copy(characters = model.characters) }
        containerHost.onLoadStatesChanged(loadStates(append = LoadState.Error(error)))
        expectInternalState {
          copy(
            contentLoadState = ContentLoadState.Error(
              error = UiError(
                messageResource = R.string.error_connection,
                cause = error,
              ),
            ),
          )
        }
        containerHost.onLoadStatesChanged(loadStates(append = LoadState.Loading))
        expectInternalState { copy(contentLoadState = ContentLoadState.Loading) }
        containerHost.onLoadStatesChanged(loadStates())
        expectInternalState { copy(contentLoadState = ContentLoadState.Ready) }
      }
    }
  }
}

private fun unusedRepository(): CharacterRepository {
  return object : CharacterRepository {
    override suspend fun readPage(page: Int): CharacterPage {
      error("UI is not collecting yet")
    }

    override suspend fun readCharacter(id: Int): Character {
      error("Unused")
    }
  }
}

private fun loadStates(
  refresh: LoadState = LoadState.NotLoading(false),
  append: LoadState = LoadState.NotLoading(false),
  prepend: LoadState = LoadState.NotLoading(false),
): CombinedLoadStates {
  return CombinedLoadStates(
    refresh = refresh,
    prepend = prepend,
    append = append,
    source = LoadStates(
      refresh = refresh,
      prepend = prepend,
      append = append,
    ),
  )
}
