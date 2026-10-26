package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Объявляет флоу — группу экранов с общим DI-графом. Флоу начинается с экрана [root] и живёт,
 * пока этот экран лежит в стеке.
 *
 * - [root] — ключ первого экрана флоу. Его запись объявляется внутри [entries], как и остальные.
 * - [component] — собирает компонент флоу [C]. Вызывается один раз, при первом показе экрана флоу.
 * - [entries] — записи флоу: `entry<…>` для каждого экрана. Внутри записи доступны методы
 *   компонента [C], и все записи флоу получают один и тот же экземпляр.
 *
 * Пример:
 *
 * ```
 * flow(
 *     root = Route.Main,
 *     component = { factory.create() },
 * ) {
 *     entry<Route.Main> {
 *         MainScreen(viewModel = viewModel { mainViewModel() })
 *     }
 * }
 * ```
 */
fun <C : FlowComponent> EntryProviderScope<NavKey>.flow(
    root: NavKey,
    component: () -> C,
    entries: FlowEntryScope<C>.() -> Unit,
) {
    val graph = FlowGraph(
        key = flowContentKey(root),
        create = component
    )
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
 * Контент записи вызывается на компоненте флоу [C]: его методы, например `mainViewModel()`,
 * доступны внутри `entry<…> { }` без дополнительных вызовов.
 */
class FlowEntryScope<C : FlowComponent> @PublishedApi internal constructor(
    @PublishedApi
    internal val scope: EntryProviderScope<NavKey>,
    @PublishedApi
    internal val root: NavKey,
    @PublishedApi
    internal val graph: FlowGraph<C>,
) {

    inline fun <reified K : NavKey> entry(
        metadata: Map<String, Any> = emptyMap(),
        noinline content: @Composable C.(K) -> Unit,
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
    internal fun <K : NavKey> withGraph(content: @Composable C.(K) -> Unit): @Composable (K) -> Unit {
        return { key -> graph.current().content(key) }
    }
}
