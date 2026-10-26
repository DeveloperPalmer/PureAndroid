package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesViewModel
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsViewModel

/**
 * ViewModel экранов флоу «Города» из его графа: они получают `@CitiesFlowScoped`-объекты флоу.
 * Hilt не разрешает методы у самого `@DefineComponent`, поэтому они достаются через эту точку входа
 */
@EntryPoint
@InstallIn(CitiesFlowComponent::class)
interface CitiesFlowEntryPoint {

    fun citiesViewModel(): CitiesViewModel

    fun cityDetailsViewModel(): CityDetailsViewModel.Factory
}
