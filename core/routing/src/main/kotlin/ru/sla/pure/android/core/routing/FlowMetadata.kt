package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata

/**
 * Ключ флоу с корнем [root].
 *
 * Корневая запись использует его как свой contentKey, а декоратор флоу берёт по нему хранилище
 * для всех записей флоу. Поэтому у корня и остальных записей одно общее хранилище флоу. Имя класса
 * в ключе нужно, чтобы он не совпал с ключом записи другого типа с таким же `toString()`.
 *
 * Ставит его [flow], фичам вызывать эту функцию не нужно.
 */
@PublishedApi
internal fun flowContentKey(root: NavKey): String = "$root:${root::class.qualifiedName ?: root.javaClass.name}"

/**
 * Метаданные записи [key] во флоу с корнем [root]: по ним декоратор флоу находит хранилище флоу,
 * собирает в нём граф через [component] и проверяет, что корень лежит в стеке под записью.
 *
 * Ставит их [flow], фичам вызывать эту функцию не нужно.
 */
@PublishedApi
internal fun partOfFlow(
  root: NavKey,
  key: NavKey,
  component: () -> FlowComponent,
): Map<String, Any> = metadata {
  put(FlowEntryKey, FlowEntry(root = root, key = key, component = component))
}

/** Флоу записи из её метаданных или `null`, если запись ни в какой флоу не входит */
internal fun flowEntry(metadata: Map<String, Any>): FlowEntry? = metadata[FlowEntryKey]

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
internal fun checkFlowRoot(metadata: Map<String, Any>, backStack: List<NavKey>) {
  val flow = flowEntry(metadata) ?: return
  if (flow.key == flow.root) return
  val index = backStack.lastIndexOf(flow.key)
  if (index == -1) return
  check(flow.root in backStack.subList(0, index)) {
    "Запись ${flow.key} входит во флоу ${flow.root}, но корня флоу нет в стеке под ней"
  }
}

/**
 * Место записи во флоу: корень флоу [root], собственный ключ записи [key] и сборка графа флоу
 * [component]
 *
 * Сохраняет сравнение регистраций по ссылке.
 */
@Suppress("UseDataClass")
internal class FlowEntry(
  val root: NavKey,
  val key: NavKey,
  val component: () -> FlowComponent,
)

private object FlowEntryKey : NavMetadataKey<FlowEntry>
