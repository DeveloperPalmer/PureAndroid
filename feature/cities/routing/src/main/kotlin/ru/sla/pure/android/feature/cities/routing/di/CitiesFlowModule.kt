package ru.sla.pure.android.feature.cities.routing.di

import dagger.Module
import dagger.hilt.InstallIn
import ru.sla.pure.android.feature.cities.ui.di.CitiesUiModule

/** Что входит в граф флоу «Города»: экраны с ViewModel из UI-модуля */
@Module(includes = [CitiesUiModule::class])
@InstallIn(CitiesFlowComponent::class)
interface CitiesFlowModule
