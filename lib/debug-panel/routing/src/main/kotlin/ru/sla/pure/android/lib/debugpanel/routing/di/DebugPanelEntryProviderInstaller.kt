package ru.sla.pure.android.lib.debugpanel.routing.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.lib.debugpanel.routing.DebugPanelFlow
import ru.sla.pure.android.lib.debugpanel.routing.DebugPanelRoute
import ru.sla.pure.android.lib.debugpanel.ui.screen.main.DebugPanelScreen

@ContributesIntoSet(ForegroundScope::class)
class DebugPanelEntryProviderInstaller @Inject constructor(
  private val componentFactory: DebugPanelFlowComponent.Factory,
) : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    flow(
      key = DebugPanelFlow.navKey,
      component = componentFactory::createDebugPanelFlowComponent
    ) {
      entry<DebugPanelRoute> {
        DebugPanelScreen(viewModel = metroViewModel())
      }
    }
  }
}
