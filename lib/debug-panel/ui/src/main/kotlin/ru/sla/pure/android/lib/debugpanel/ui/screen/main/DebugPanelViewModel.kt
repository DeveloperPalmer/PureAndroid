package ru.sla.pure.android.lib.debugpanel.ui.screen.main

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.buildconfig.BuildConfigProvider
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.lib.debugpanel.domain.di.DebugPanelScope
import ru.sla.pure.android.lib.debugpanel.ui.routing.FlowEvent

@ViewModelKey
@ContributesIntoMap(DebugPanelScope::class, binding<ViewModel>())
class DebugPanelViewModel @Inject constructor(
  buildConfigProvider: BuildConfigProvider,
  private val events: FlowEventSink,
) : BaseViewModel<String, Nothing>() {
  override val container = orbitContainer<String, Nothing>(
    initialState = buildConfigProvider.buildType.name.lowercase()
  )

  fun onCloseRequested() = intent {
    events.sendEvent(FlowEvent.DebugPanelDismissed)
  }
}
