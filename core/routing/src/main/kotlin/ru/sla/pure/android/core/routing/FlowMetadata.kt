package ru.sla.pure.android.core.routing

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata

/**
 * Ключ метаданных, которым запись указывает свой флоу.
 *
 * Значение — ключ флоу (см. [flowContentKey]). По нему декоратор навигации находит хранилище
 * флоу. Запись без этого ключа ни в какой флоу не входит.
 */
object FlowKey : NavMetadataKey<String>

/**
 * Ключ флоу с корнем [root].
 *
 * Корневая запись использует его как свой contentKey, а остальные записи флоу хранят его в
 * [FlowKey]. Поэтому у корня и остальных записей одно общее хранилище флоу. Имя класса в ключе
 * нужно, чтобы он не совпал с ключом записи другого типа с таким же `toString()`.
 *
 * Ставит его [flow], фичам вызывать эту функцию не нужно.
 */
@PublishedApi
internal fun flowContentKey(root: NavKey): String = "$root:${root::class.qualifiedName}"

/**
 * Метаданные записи, которая входит во флоу с корнем [root]: ключ флоу в [FlowKey].
 *
 * Ставит их [flow], фичам вызывать эту функцию не нужно.
 */
@PublishedApi
internal fun partOfFlow(root: NavKey): Map<String, Any> = metadata { put(FlowKey, flowContentKey(root)) }

/**
 * Хранилище ViewModel флоу для текущей записи.
 *
 * - У записи флоу это хранилище корня флоу: одно на все записи флоу, живёт, пока корень в стеке.
 * - У записи вне флоу это её собственное хранилище.
 *
 * Задаёт его декоратор навигации. Без декоратора обращение к нему падает с ошибкой.
 */
val LocalFlowViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("LocalFlowViewModelStoreOwner не задан: нужен декоратор хранилища флоу")
}
