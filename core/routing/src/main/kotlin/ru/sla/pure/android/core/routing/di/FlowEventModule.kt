package ru.sla.pure.android.core.routing.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.Multibinds
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowEventMediator
import ru.sla.pure.android.core.routing.FlowEventSource
import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.core.ui.FlowEventSink

@Module
@InstallIn(ActivityRetainedComponent::class)
interface FlowEventModule {

    // Один экземпляр на две роли: ViewModel пишут в него как в FlowEventSink,
    // приложение читает как FlowEventSource
    @Binds
    fun bindFlowEventSink(mediator: FlowEventMediator): FlowEventSink

    @Binds
    fun bindFlowEventSource(mediator: FlowEventMediator): FlowEventSource

    // Объявляют наборы, даже если ни одна фича в них ничего не положила: иначе пустой Set
    // в графе не существует, и Dagger не соберёт приложение
    @Multibinds
    fun entryProviderInstallers(): Set<EntryProviderInstaller>

    @Multibinds
    fun flowNodes(): Set<FlowNode>
}
