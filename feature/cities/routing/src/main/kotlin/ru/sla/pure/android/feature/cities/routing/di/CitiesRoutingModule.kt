package ru.sla.pure.android.feature.cities.routing.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.feature.cities.routing.CitiesEntryProviderInstaller
import ru.sla.pure.android.feature.cities.routing.CitiesFlowNode

@Module
@InstallIn(ActivityRetainedComponent::class)
interface CitiesRoutingModule {

    @Binds
    @IntoSet
    fun bindFlowNode(flowNode: CitiesFlowNode): FlowNode

    @Binds
    @IntoSet
    fun bindEntryProviderInstaller(installer: CitiesEntryProviderInstaller): EntryProviderInstaller
}
