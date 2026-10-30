package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Объявляет флоу — группу экранов с общим DI-графом. Флоу начинается с экрана [root] и живёт,
 * пока этот экран лежит в стеке.
 *
 * - [root] — ключ первого экрана флоу. Его запись объявляется внутри [entries], как и остальные.
 * - [component] — собирает граф флоу. Вызывается один раз, при первом показе экрана флоу.
 * - [entries] — записи флоу: `entry<…>` для каждого экрана. Внутри записи `metroViewModel()`
 *   создаёт ViewModel из графа флоу, и все записи флоу берут их из одного и того же графа.
 *
 * Пример:
 *
 * ```
 * flow(
 *     root = Route.Main,
 *     component = { factory.create() },
 * ) {
 *     entry<Route.Main> {
 *         MainScreen(viewModel = metroViewModel())
 *     }
 * }
 * ```
 */
fun EntryProviderScope<NavKey>.flow(
  root: NavKey,
  component: () -> FlowComponent,
  entries: FlowEntryScope.() -> Unit,
) {
  FlowEntryScope(this, root, component).entries()
}

/**
 * Область внутри [flow], где объявляются экраны флоу через [entry].
 *
 * Метаданные флоу ставятся сами, вручную их задавать не нужно: каждая запись получает корень флоу,
 * свой ключ навигации и сборку графа флоу. По ним декоратор флоу ([FlowNavEntryDecorator]) находит
 * общее хранилище флоу, берёт из него граф и проверяет, что корень лежит в стеке под записью.
 * Корневая запись вдобавок получает ключ флоу как свой contentKey: так хранилище флоу — это
 * хранилище корня, и оно очищается, когда корень уходит из стека.
 */
class FlowEntryScope internal constructor(
  @PublishedApi
  internal val scope: EntryProviderScope<NavKey>,
  @PublishedApi
  internal val root: NavKey,
  @PublishedApi
  internal val component: () -> FlowComponent,
) {
  inline fun <reified K : NavKey> entry(
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable (K) -> Unit,
  ) {
    if (K::class == root::class) {
      scope.entry<K>(
        clazzContentKey = ::flowContentKey,
        metadata = { key -> metadata + partOfFlow(root, key, component) },
        content = content,
      )
    } else {
      scope.entry<K>(
        metadata = { key -> metadata + partOfFlow(root, key, component) },
        content = content,
      )
    }
  }
}
