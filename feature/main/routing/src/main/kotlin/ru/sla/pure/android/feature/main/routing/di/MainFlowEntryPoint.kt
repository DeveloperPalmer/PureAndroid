package ru.sla.pure.android.feature.main.routing.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import ru.sla.pure.android.feature.main.ui.screen.main.MainViewModel

@EntryPoint
@InstallIn(MainFlowComponent::class)
interface MainFlowEntryPoint {

    fun mainViewModel(): MainViewModel
}
