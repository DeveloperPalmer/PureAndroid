package ru.sla.pure.android.feature.map.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.map.ui.screen.map.MapScreen

@ContributesIntoSet(AppScope::class)
class MapEntryProviderInstaller @Inject constructor() : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        entry<MapRoute.Map>(metadata = showNavigationBar()) {
            MapScreen()
        }
    }
}
