package ru.sla.pure.android.feature.app.ui.screen.initialresolve

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.auth.session.domain.AuthSessionModel
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.app.ui.routing.FlowEvent

@ViewModelKey(InitialResolveViewModel::class)
@ContributesIntoMap(AppFlowScope::class, binding<ViewModel>())
class InitialResolveViewModel @Inject constructor(
  sessionModel: AuthSessionModel,
  @ForScope(AppFlowScope::class)
  events: FlowEventSink,
) : BaseViewModel<Unit, Nothing>() {
  override val container = orbitContainer<Unit, Nothing>(
    initialState = Unit,
    buildSettings = { intentLaunchingDispatcher = { Dispatchers.Main.immediate } },
  ) {
    val sessionState = try {
      sessionModel.sessionState.first()
    } catch (exception: CancellationException) {
      throw exception
    } catch (_: Exception) {
      AuthSessionState.Inactive
    }
    val event = when (sessionState) {
      AuthSessionState.Active -> FlowEvent.ActiveSessionResolved
      AuthSessionState.Inactive -> FlowEvent.InactiveSessionResolved
    }
    events.sendEvent(event)
  }
}
