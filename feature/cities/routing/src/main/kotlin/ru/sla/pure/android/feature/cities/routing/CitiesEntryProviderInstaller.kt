package ru.sla.pure.android.feature.cities.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flowContentKey
import ru.sla.pure.android.core.routing.partOfFlow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.cities.routing.di.CitiesFlowGraph
import javax.inject.Inject

/**
 * Экраны фичи «Города»: какой экран показать по ключу. Список — корень флоу «Города», детали
 * живут внутри него. Экраны с ViewModel берутся из графа флоу, здесь их только показывают
 */
class CitiesEntryProviderInstaller @Inject constructor(
    private val flow: CitiesFlowGraph,
) : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        entry<CitiesRoute.Cities>(
            metadata = showNavigationBar(),
            clazzContentKey = ::flowContentKey,
        ) {
            flow.current().cities().Content(Unit)
        }
        entry<CitiesRoute.CityDetails>(
            metadata = partOfFlow(CitiesRoute.Cities)
        ) { route ->
            flow.current().cityDetails().Content(route.cityId)
        }
    }
}
