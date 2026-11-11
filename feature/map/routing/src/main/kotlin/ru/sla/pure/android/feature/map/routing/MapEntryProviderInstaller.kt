package ru.sla.pure.android.feature.map.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.map.ui.screen.map.MapScreen

object MapEntryProviderInstaller : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    entry<MapRoute.Map>(metadata = showNavigationBar()) {
      MapScreen()
    }
  }
}
