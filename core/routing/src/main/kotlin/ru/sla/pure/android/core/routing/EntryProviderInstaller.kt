package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Записи `entry<…>` модуля routing фичи. Каждый модуль кладёт свой установщик в общий `Set`
 * через Hilt (`@Provides @IntoSet`), а приложение собирает из набора `entryProvider` и не знает
 * экранов фич. Навигатор установщику не нужен: экраны не навигируют, а шлют события
 */
fun interface EntryProviderInstaller {

    fun EntryProviderScope<NavKey>.install()
}
