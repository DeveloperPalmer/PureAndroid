package ru.sla.pure.android.feature.cities.routing

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.cities.routing.di.CitiesFlowComponent
import ru.sla.pure.android.feature.cities.routing.di.CitiesFlowEntryPoint
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen
import javax.inject.Inject
import javax.inject.Provider

/**
 * Экраны фичи «Города»: какой экран показать по ключу. Список — корень флоу «Города», детали
 * живут внутри него. ViewModel экранов берутся из графа флоу, а живут в хранилище своей записи
 */
class CitiesEntryProviderInstaller @Inject constructor(
    private val component: Provider<CitiesFlowComponent.Builder>,
) : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        flow<CitiesFlowEntryPoint>(
            root = CitiesRoute.Cities,
            component = { component.get().build() },
        ) {
            entry<CitiesRoute.Cities>(metadata = showNavigationBar()) {
                CitiesScreen(viewModel = viewModel { citiesViewModel() })
            }
            entry<CitiesRoute.CityDetails> { route ->
                CityDetailsScreen(viewModel = viewModel { cityDetailsViewModel().create(route.cityId) })
            }
        }
    }
}
