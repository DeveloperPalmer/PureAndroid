package ru.sla.pure.android.core.routing

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
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
 * Метаданные записи [key], которая входит во флоу с корнем [root]: ключ флоу в [FlowKey] и место
 * записи во флоу для [checkFlowRoot].
 *
 * Ставит их [flow], фичам вызывать эту функцию не нужно.
 */
@PublishedApi
internal fun partOfFlow(root: NavKey, key: NavKey): Map<String, Any> = metadata {
    put(FlowKey, flowContentKey(root))
    put(FlowPlacementKey, FlowPlacement(root = root, key = key))
}

/**
 * Проверяет, что корень флоу записи лежит в [backStack] ниже неё, и падает с ошибкой, если нет.
 *
 * Хранилище флоу очищается, когда из стека уходит корень. Запись флоу без корня под ней создала бы
 * хранилище, которое никто не очистит: граф флоу и его scope жили бы до конца Activity, а следующий
 * корень этого флоу получил бы старый граф.
 *
 * Проверку сразу проходят запись вне флоу, корень флоу и запись, которой уже нет в стеке: её убрали,
 * а `NavDisplay` ещё показывает её на анимации ухода.
 */
fun checkFlowRoot(metadata: Map<String, Any>, backStack: List<NavKey>) {
    val placement = metadata[FlowPlacementKey] ?: return
    val index = backStack.lastIndexOf(placement.key)
    if (index == -1) return
    check(placement.root in backStack.subList(0, index)) {
        "Запись ${placement.key} входит во флоу ${placement.root}, но корня флоу нет в стеке под ней"
    }
}

/** Ключ метаданных: корень флоу записи и её собственный ключ навигации, нужны [checkFlowRoot] */
private object FlowPlacementKey : NavMetadataKey<FlowPlacement>

private data class FlowPlacement(
    val root: NavKey,
    val key: NavKey,
)

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
