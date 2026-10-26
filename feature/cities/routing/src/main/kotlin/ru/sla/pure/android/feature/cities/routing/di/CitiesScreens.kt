package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import ru.sla.pure.android.core.ui.screen.WiredComposableScreen
import ru.sla.pure.android.feature.cities.ui.di.Screen
import ru.sla.pure.android.feature.cities.ui.di.WiredScreen

/**
 * Экраны флоу «Города», которые роутинг показывает по ключам навигации. Hilt не разрешает методы
 * у самого `@DefineComponent`, поэтому экраны достаются из компонента через эту точку входа
 */
@EntryPoint
@InstallIn(CitiesFlowComponent::class)
interface CitiesScreens {

    @WiredScreen(Screen.Cities)
    fun cities(): WiredComposableScreen<Unit>

    @WiredScreen(Screen.CityDetails)
    fun cityDetails(): WiredComposableScreen<Long>
}
