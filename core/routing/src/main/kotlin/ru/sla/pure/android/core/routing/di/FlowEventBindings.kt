package ru.sla.pure.android.core.routing.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowEventMediator
import ru.sla.pure.android.core.routing.FlowEventSource
import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.core.ui.FlowEventSink

@ContributesTo(AppScope::class)
interface FlowEventBindings {

    // FlowEventMediator в двух ролях: ViewModel отправляют в него события как в FlowEventSink,
    // а приложение читает их из него как из FlowEventSource. Обе привязки ведут к одному экземпляру
    @Binds
    val FlowEventMediator.bindFlowEventSink: FlowEventSink

    @Binds
    val FlowEventMediator.bindFlowEventSource: FlowEventSource

    // Объявляют наборы заранее. Пустой набор в Metro — ошибка, пока он не объявлен с allowEmpty:
    // без этого приложение без фич с навигацией не собралось бы
    @Multibinds(allowEmpty = true)
    fun entryProviderInstallers(): Set<EntryProviderInstaller>

    @Multibinds(allowEmpty = true)
    fun flowNodes(): Set<FlowNode>
}
