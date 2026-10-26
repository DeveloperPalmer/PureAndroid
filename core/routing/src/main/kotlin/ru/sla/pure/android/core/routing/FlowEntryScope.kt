package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.EntryPoints

/**
 * Записи флоу с корнем [root]. [component] собирает компонент флоу, а записи получают из него
 * точку входа [E] — один экземпляр на флоу, пока корень в стеке:
 *
 * ```
 * flow<CitiesFlowEntryPoint>(root = CitiesRoute.Cities, component = { builder.get().build() }) {
 *     entry<CitiesRoute.Cities> { CitiesScreen(viewModel = viewModel { citiesViewModel() }) }
 * }
 * ```
 */
inline fun <reified E : Any> EntryProviderScope<NavKey>.flow(
    root: NavKey,
    noinline component: () -> Any,
    entries: FlowEntryScope<E>.() -> Unit,
) {
    val graph = FlowGraph(flowContentKey(root)) {
        EntryPoints.get(
            component(),
            E::class.java
        )
    }
    FlowEntryScope(this, root, graph).entries()
}

/**
 * Записи внутри [flow]. Метаданные флоу ставятся сами: корень получает contentKey флоу, остальные
 * записи — ссылку на корень. Контент записи вызывается на точке входа флоу [E], поэтому её
 * методы доступны в нём напрямую
 */
class FlowEntryScope<E : Any> @PublishedApi internal constructor(
    @PublishedApi internal val scope: EntryProviderScope<NavKey>,
    @PublishedApi internal val root: NavKey,
    @PublishedApi internal val graph: FlowGraph<E>,
) {

    inline fun <reified K : NavKey> entry(
        metadata: Map<String, Any> = emptyMap(),
        noinline content: @Composable E.(K) -> Unit,
    ) {
        if (K::class == root::class) {
            scope.entry<K>(
                clazzContentKey = ::flowContentKey,
                metadata = metadata,
                content = withGraph(content),
            )
        } else {
            scope.entry<K>(
                metadata = metadata + partOfFlow(root),
                content = withGraph(content),
            )
        }
    }

    // Composable-лямбда собирается здесь, а не в inline entry: так её компилирует этот модуль
    @PublishedApi
    internal fun <K : NavKey> withGraph(content: @Composable E.(K) -> Unit): @Composable (K) -> Unit = { key ->
        graph.current().content(key)
    }
}
