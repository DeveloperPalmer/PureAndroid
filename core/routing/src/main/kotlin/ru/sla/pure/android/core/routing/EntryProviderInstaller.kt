package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Экраны одной фичи: какой экран показать по какому ключу навигации и куда вести по его колбэкам.
 * В [install] фича объявляет свои записи `entry<…>` или `flow { }` и превращает колбэки экранов
 * в переходы через [Navigator]. Экраны модуля ui получают только колбэки, поэтому ключей навигации
 * не знают, а решение о переходе остаётся в модуле routing.
 *
 * Как попадает в приложение: модуль routing фичи кладёт свой установщик в общий
 * `Set<EntryProviderInstaller>` через `@ContributesIntoSet`. Приложение проходит по набору и собирает
 * из него один `entryProvider`, поэтому про экраны фич оно ничего не знает.
 */
fun interface EntryProviderInstaller {
  fun EntryProviderScope<NavKey>.install(navigator: Navigator)
}
