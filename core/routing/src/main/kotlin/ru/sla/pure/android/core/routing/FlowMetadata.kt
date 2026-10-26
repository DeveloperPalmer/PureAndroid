package ru.sla.pure.android.core.routing

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata

/** Ключ метаданных записи: contentKey корневой записи флоу, к которому относится запись */
object FlowKey : NavMetadataKey<String>

/**
 * contentKey корневой записи флоу. Корень задаёт его явно (`clazzContentKey = ::flowContentKey`),
 * а записи флоу ссылаются на него через [partOfFlow]: по этому ключу находится хранилище флоу.
 * Обе метаданные ставит [flow], фичам вызывать их не нужно
 */
@PublishedApi
internal fun flowContentKey(root: NavKey): String = "$root:${root::class.qualifiedName}"

/** Метаданные записи внутри флоу с корнем [root] */
@PublishedApi
internal fun partOfFlow(root: NavKey): Map<String, Any> = metadata { put(FlowKey, flowContentKey(root)) }

/**
 * Хранилище ViewModel флоу — хранилище его корневой записи. ViewModel в нём живёт, пока корень
 * в стеке, и общая для всех записей флоу. У записи вне флоу это её собственное хранилище
 */
val LocalFlowViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("LocalFlowViewModelStoreOwner не задан: нужен декоратор хранилища флоу")
}
