package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey

/**
 * Переходы между экранами. Его получают модули routing фич в [EntryProviderInstaller.install]
 * и вызывают из колбэков экранов: сами экраны и ViewModel о навигации не знают
 */
interface Navigator {

    /**
     * Открыть экран [route]:
     * - если это ключ вкладки, переключается вкладка;
     * - иначе экран добавляется в стек текущей вкладки.
     */
    fun navigateTo(route: NavKey)

    /** Шаг назад — то же, что системная кнопка «Назад» */
    fun navigateBack()
}
