package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.EntryPoints

/**
 * Объявляет флоу — группу экранов с общим DI-графом. Флоу начинается с экрана [root] и живёт,
 * пока этот экран лежит в стеке.
 *
 * - [root] — ключ первого экрана флоу. Его запись объявляется внутри [entries], как и остальные.
 * - [component] — собирает Hilt-компонент флоу. Вызывается один раз, при первом показе экрана флоу.
 * - [E] — точка входа в этот компонент. Внутри каждой записи доступны её методы, и все записи флоу
 *   получают один и тот же экземпляр.
 * - [entries] — записи флоу: `entry<…>` для каждого экрана.
 *
 * Пример:
 *
 * ```
 * flow<SomeFlowEntryPoint>(
 *     root = Route.Main,
 *     component = { builder.get().build() },
 * ) {
 *     entry<Route.Main> {
 *         MainScreen(viewModel = viewModel { mainViewModel() })
 *     }
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
 * Область внутри [flow], где объявляются экраны флоу через [entry].
 *
 * Метаданные флоу ставятся сами, вручную их задавать не нужно:
 * - корневая запись получает ключ флоу;
 * - остальные записи получают ссылку на корень и свой ключ навигации.
 * По ссылке на корень декоратор навигации находит общее хранилище флоу, в котором живёт его граф,
 * а по обоим ключам проверяет, что корень лежит в стеке под записью ([checkFlowRoot]).
 *
 * Контент записи вызывается на точке входа флоу [E]: её методы, например `mainViewModel()`,
 * доступны внутри `entry<…> { }` без дополнительных вызовов.
 */
class FlowEntryScope<E : Any> @PublishedApi internal constructor(
    @PublishedApi
    internal val scope: EntryProviderScope<NavKey>,
    @PublishedApi
    internal val root: NavKey,
    @PublishedApi
    internal val graph: FlowGraph<E>,
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
                metadata = { key -> metadata + partOfFlow(root, key) },
                content = withGraph(content),
            )
        }
    }

    // Composable-лямбда создаётся в обычной функции, а не в inline entry: так её обрабатывает
    // Compose-компилятор этого модуля, а не модуля фичи, куда встраивается entry
    @PublishedApi
    internal fun <K : NavKey> withGraph(content: @Composable E.(K) -> Unit): @Composable (K) -> Unit {
        return { key -> graph.current().content(key) }
    }
}
