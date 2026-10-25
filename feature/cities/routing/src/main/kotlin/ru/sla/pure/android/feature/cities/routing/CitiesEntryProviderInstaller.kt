package ru.sla.pure.android.feature.cities.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen
import javax.inject.Inject

/** Экраны фичи «Города»: какой экран показать по ключу */
class CitiesEntryProviderInstaller @Inject constructor() : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        entry<CitiesRoute.Cities>(metadata = showNavigationBar()) {
            CitiesScreen()
        }
        entry<CitiesRoute.CityDetails> { route ->
            CityDetailsScreen(
                cityId = route.cityId
            )
        }
    }
}
