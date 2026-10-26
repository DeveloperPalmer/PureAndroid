package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Экраны одной фичи: какой экран показать по какому ключу навигации. В [install] фича объявляет
 * свои записи `entry<…>` или `flow { }`.
 *
 * Как попадает в приложение: модуль routing фичи кладёт свой установщик в общий
 * `Set<EntryProviderInstaller>` через `@Binds @IntoSet`. Приложение проходит по набору и собирает
 * из него один `entryProvider`, поэтому про экраны фич оно ничего не знает.
 *
 * Навигатор здесь не нужен: экраны сами никуда не переходят, а шлют события, и переход по ним
 * выбирает [FlowNode]
 */
fun interface EntryProviderInstaller {

    fun EntryProviderScope<NavKey>.install()
}
