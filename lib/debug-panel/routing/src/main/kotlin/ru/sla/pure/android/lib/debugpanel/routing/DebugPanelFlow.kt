package ru.sla.pure.android.lib.debugpanel.routing

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.lib.debugpanel.domain.di.DebugPanelScope
import ru.sla.pure.android.lib.debugpanel.ui.routing.FlowEvent

@SingleIn(DebugPanelScope::class)
@ContributesBinding(DebugPanelScope::class)
class DebugPanelFlow @Inject constructor(
  private val debugPanelAdapter: DebugPanelAdapter,
) : Flow {
  override fun onEntry(): List<NavKey> {
    return listOf(DebugPanelRoute)
  }

  override fun transition(event: Event) {
    when (event) {
      is FlowEvent.DebugPanelDismissed -> debugPanelAdapter.close()
    }
  }

  companion object {
    val navKey = FlowNavKey("debugPanel")
  }
}
