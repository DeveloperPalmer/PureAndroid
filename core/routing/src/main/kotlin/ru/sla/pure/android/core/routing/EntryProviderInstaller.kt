package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Экраны одной фичи: какой экран показать по какому ключу навигации.
 * В [install] фича объявляет записи `entry<…>` или `flow { }`.
 *
 * Как попадает в приложение: модуль routing фичи кладёт свой установщик в общий
 * `Set<EntryProviderInstaller>` через `@ContributesIntoSet`. Приложение проходит по набору и собирает
 * из него один `entryProvider`, поэтому про экраны фич оно ничего не знает.
 */
fun interface EntryProviderInstaller {
  fun EntryProviderScope<NavKey>.install()
}
