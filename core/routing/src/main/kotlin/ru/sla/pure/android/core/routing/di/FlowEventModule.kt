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

    // FlowEventMediator в двух ролях: ViewModel отправляют в него события как в FlowEventSink,
    // а приложение читает их из него как из FlowEventSource. Обе привязки ведут к одному экземпляру
    @Binds
    fun bindFlowEventSink(mediator: FlowEventMediator): FlowEventSink

    @Binds
    fun bindFlowEventSource(mediator: FlowEventMediator): FlowEventSource

    // Объявляют наборы заранее. Если ни одна фича ничего в набор не положила, без этого
    // объявления пустого Set в графе нет, и Dagger не соберёт приложение
    @Multibinds
    fun entryProviderInstallers(): Set<EntryProviderInstaller>

    @Multibinds
    fun flowNodes(): Set<FlowNode>
}
