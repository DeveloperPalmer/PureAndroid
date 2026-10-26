package ru.sla.pure.android.feature.cities.routing

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flowContentKey
import ru.sla.pure.android.core.routing.partOfFlow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsViewModel
import javax.inject.Inject

/**
 * Экраны фичи «Города»: какой экран показать по ключу. Список — корень флоу «Города», детали
 * живут внутри него и делят с ним хранилище флоу
 */
class CitiesEntryProviderInstaller @Inject constructor() : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        entry<CitiesRoute.Cities>(
            metadata = showNavigationBar(),
            clazzContentKey = ::flowContentKey,
        ) {
            CitiesScreen(viewModel = hiltViewModel())
        }
        entry<CitiesRoute.CityDetails>(metadata = partOfFlow(CitiesRoute.Cities)) { route ->
            val viewModel = hiltViewModel<CityDetailsViewModel, CityDetailsViewModel.Factory> { factory ->
                factory.create(route.cityId)
            }
            CityDetailsScreen(viewModel = viewModel)
        }
    }
}
