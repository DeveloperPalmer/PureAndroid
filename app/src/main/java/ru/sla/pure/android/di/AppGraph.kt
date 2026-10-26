package ru.sla.pure.android.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import ru.sla.pure.android.core.routing.EntryProviderInstaller

/**
 * Корневой граф: живёт столько же, сколько процесс. `@DependencyGraph(AppScope::class)` добавляет
 * в него всё, что модули вложили в `AppScope`: привязки, наборы и фабрики графов флоу. Поэтому
 * здесь перечислено только то, что нужно самому приложению
 */
@DependencyGraph(AppScope::class)
interface AppGraph {

    @Multibinds(allowEmpty = true)
    fun entryProviderInstallers(): Set<EntryProviderInstaller>
}
