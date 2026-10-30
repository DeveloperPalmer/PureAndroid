package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata

/** Ключ метаданных: `true`, если запись показывается с нижней панелью навигации. */
object NavigationBarKey : NavMetadataKey<Boolean>

/**
 * Метаданные записи, которая показывается с нижней панелью навигации.
 *
 * Пример: `entry<Route.Main>(metadata = showNavigationBar())`.
 */
fun showNavigationBar(): Map<String, Any> = metadata { put(NavigationBarKey, true) }
