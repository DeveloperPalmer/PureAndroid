package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope

@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class MainViewModel @Inject constructor(
    private val mainModel: MainModel,
) : BaseViewModel<ViewState, Nothing>() {
    private val loading = Mutex()

    override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
        refresh()
    }

    fun refresh() = load { 1 }
    fun loadMore() = load { nextPage }
    fun retry() = load { failedPage }

    private fun load(page: ViewState.() -> Int?) = intent {
        if (!loading.tryLock()) return@intent
        try {
            val requestedPage = state.page() ?: return@intent
            reduce { state.copy(isLoading = true, error = null, failedPage = null) }
            try {
                val result = mainModel.readPage(requestedPage)
                reduce {
                    state.copy(
                        characters = if (requestedPage == 1) result.characters else
                            (state.characters + result.characters).distinctBy { it.id },
                        isLoading = false,
                        nextPage = result.nextPage,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                reduce {
                    state.copy(
                        isLoading = false, failedPage = requestedPage,
                        error = when (error) {
                            is ServerException.Unreachable -> LoadError.Connection
                            is ServerException -> LoadError.Server
                            else -> LoadError.Unexpected
                        },
                    )
                }
            }
        } finally {
            loading.unlock()
        }
    }
}
