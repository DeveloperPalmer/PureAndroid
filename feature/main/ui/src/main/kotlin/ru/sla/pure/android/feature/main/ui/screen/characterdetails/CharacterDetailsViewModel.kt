package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.kode.remo.QueueingStrategy
import ru.kode.remo.StartScheduled
import ru.kode.remo.successResults
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.lce.asLceState
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent

class CharacterDetailsViewModel @AssistedInject constructor(
  @Assisted
  private val characterId: Int,
  private val mainModel: MainModel,
  private val flowEventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {
  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(
    MainScope::class,
    binding<ManualViewModelAssistedFactory>()
  )
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(characterId: Int): CharacterDetailsViewModel
  }

  override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
    // Both asLceState and successResults must subscribe before a fast response can be emitted.
    mainModel.loadCharacter.start(
      argument = characterId,
      scheduled = StartScheduled.Lazily(minResultsSubscribers = 2),
      queueingStrategy = QueueingStrategy.SkipNew,
    )

    mainModel.loadCharacter.jobFlow
      .asLceState()
      .onEach { reduce { state.copy(contentLoadState = it) } }
      .launchIn(viewModelScope)

    mainModel.loadCharacter.jobFlow.successResults()
      .onEach { reduce { state.copy(character = it) } }
      .launchIn(viewModelScope)
  }

  fun onNavigateBack() = intent {
    flowEventSink.sendEvent(MainFlowEvent.CharacterDetailsDismissed)
  }

  fun onRetry() = intent {
    mainModel.loadCharacter.start(
      argument = characterId,
      queueingStrategy = QueueingStrategy.SkipNew
    )
  }
}
