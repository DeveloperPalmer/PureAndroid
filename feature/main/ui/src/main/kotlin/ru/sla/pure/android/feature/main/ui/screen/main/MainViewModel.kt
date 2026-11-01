package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.core.ui.toUiLceState
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.domain.paging.asLceState

@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class MainViewModel @Inject constructor(
  mainModel: MainModel,
) : BaseViewModel<ViewState, Nothing>() {
  override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
    reduce { state.copy(characters = mainModel.characters) }
  }

  fun onLoadStatesChanged(loadStates: CombinedLoadStates): Job {
    return intent {
      val contentLoadState = loadStates
        .asLceState()
        .toUiLceState()
      reduce {
        state.copy(
          contentLoadState = contentLoadState,
          refreshInProgress = loadStates.refresh is LoadState.Loading,
        )
      }
    }
  }
}
