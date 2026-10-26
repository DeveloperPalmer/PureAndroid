package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesViewModel
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsViewModel

@EntryPoint
@InstallIn(CitiesFlowComponent::class)
interface CitiesFlowEntryPoint {

    fun citiesViewModel(): CitiesViewModel

    fun cityDetailsViewModel(): CityDetailsViewModel.Factory
}
