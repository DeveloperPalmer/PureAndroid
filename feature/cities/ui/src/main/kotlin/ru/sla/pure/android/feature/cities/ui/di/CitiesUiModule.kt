package ru.sla.pure.android.feature.cities.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import ru.sla.pure.android.core.ui.screen.WiredComposableScreen
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesViewModel
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsViewModel
import javax.inject.Provider
import javax.inject.Qualifier

/**
 * Экраны фичи «Города» вместе с их ViewModel. Модуль не знает, в какой граф попадёт: его подключает
 * роутинг через `includes` в граф флоу, поэтому ViewModel получают `@CitiesFlowScoped`-объекты флоу
 */
@Module
@DisableInstallInCheck
object CitiesUiModule {

    @Provides
    @WiredScreen(Screen.Cities)
    fun provideCitiesScreen(viewModel: Provider<CitiesViewModel>): WiredComposableScreen<Unit> {
        return WiredComposableScreen.bind(viewModel::get) { CitiesScreen(viewModel = it) }
    }

    @Provides
    @WiredScreen(Screen.CityDetails)
    fun provideCityDetailsScreen(factory: CityDetailsViewModel.Factory): WiredComposableScreen<Long> {
        return WiredComposableScreen.bind(factory::create) { CityDetailsScreen(viewModel = it) }
    }
}

@Qualifier
annotation class WiredScreen(val screen: Screen)

// Все экраны, которые поставляет этот UI-модуль, перечисляются здесь
enum class Screen {
    Cities,
    CityDetails,
}
