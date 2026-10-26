package ru.sla.pure.android.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowEventSource
import ru.sla.pure.android.core.routing.FlowNode

/**
 * Корневой граф: живёт столько же, сколько процесс. `@DependencyGraph(AppScope::class)` добавляет
 * в него всё, что модули вложили в `AppScope`: привязки, наборы и фабрики графов флоу. Поэтому
 * здесь перечислено только то, что нужно самому приложению
 */
@DependencyGraph(AppScope::class)
interface AppGraph {

    fun entryProviderInstallers(): Set<EntryProviderInstaller>

    fun flowNodes(): Set<FlowNode>

    /** События экранов, которые ViewModel отправили через FlowEventSink */
    fun flowEventSource(): FlowEventSource
}
