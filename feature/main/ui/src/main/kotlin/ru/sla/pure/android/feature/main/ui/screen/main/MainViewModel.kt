package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.core.ui.toUiLceState
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.domain.paging.asLceState
import ru.sla.pure.android.feature.main.ui.routing.FlowEvent

@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class MainViewModel @Inject constructor(
  mainModel: MainModel,
  private val flowEventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {
  override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
    reduce { state.copy(characters = mainModel.characters) }
  }

  fun onOpenCharacter(characterId: Int) = intent {
    flowEventSink.sendEvent(FlowEvent.CharacterDetailsRequested(characterId))
  }

  fun onOpenProfile() = intent {
    flowEventSink.sendEvent(FlowEvent.ProfileRequested)
  }

  fun onLoadStatesChanged(loadStates: CombinedLoadStates) = intent {
    val contentLoadState = loadStates
      .asLceState()
      .toUiLceState()
    reduce {
      state.copy(
        contentLoadState = contentLoadState,
        refreshing = loadStates.refresh is LoadState.Loading,
      )
    }
  }

  fun onCharactersCountChanged(count: Int) = intent {
    reduce { state.copy(hasContent = count > 0) }
  }
}
