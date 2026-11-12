package ru.sla.pure.android.feature.main.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.kode.remo.QueueingStrategy
import ru.kode.remo.errors
import ru.kode.remo.successResults
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.ui.routing.FlowEvent

@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class ProfileViewModel @Inject constructor(
  private val mainModel: MainModel,
  private val flowEventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {
  override val container = orbitContainer<ViewState, Nothing>(
    initialState = ViewState(),
    buildSettings = { intentLaunchingDispatcher = { Dispatchers.Main.immediate } },
  ) {
    mainModel.logout.jobFlow
      .successResults()
      .onEach { flowEventSink.sendEvent(FlowEvent.LogoutSucceeded) }
      .launchIn(viewModelScope)

    mainModel.logout.jobFlow
      .errors()
      .onEach {
        reduce {
          state.copy(
            logoutFailed = true,
            logoutInProgress = false
          )
        }
      }
      .launchIn(viewModelScope)
  }

  fun onNavigateBack() = intent {
    if (!state.logoutInProgress) {
      flowEventSink.sendEvent(FlowEvent.ProfileDismissed)
    }
  }

  fun onLogout() = intent {
    if (state.logoutInProgress) {
      return@intent
    }
    reduce {
      state.copy(
        logoutFailed = false,
        logoutInProgress = true
      )
    }
    mainModel.logout.start(queueingStrategy = QueueingStrategy.SkipNew)
  }
}
