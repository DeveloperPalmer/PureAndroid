package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata

/** Ключ метаданных записи: `true` — запись показывается с нижней панелью */
object NavigationBarKey : NavMetadataKey<Boolean>

/** Метаданные записи, которая показывается с нижней панелью: `entry<…>(metadata = showNavigationBar())` */
fun showNavigationBar(): Map<String, Any> = metadata { put(NavigationBarKey, true) }
