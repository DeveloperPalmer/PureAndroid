package ru.sla.pure.android.app.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import ru.sla.pure.android.core.routing.FlowNavKey

/**
 * Состояние вкладок: у каждой вкладки свой стек, и они живут параллельно. Переключение вкладки
 * сохраняет историю. Каждый стек и текущая вкладка сохраняются через сериализацию ключей,
 * поэтому переживают пересоздание Activity и смерть процесса.
 */
class NavigationState(
  val startRoute: NavKey,
  topLevelRoute: MutableState<NavKey>,
  val topLevelBackStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
  var topLevelRoute: NavKey by topLevelRoute

  private val activeStack: NavBackStack<NavKey>
    get() = topLevelBackStacks.getValue(topLevelRoute)

  internal val currentRoute: NavKey?
    get() = activeStack.lastOrNull()

  internal val firstRoute: NavKey?
    get() = activeStack.firstOrNull()

  internal fun setRoot(route: NavKey) {
    if (activeStack.singleOrNull() == route && topLevelBackStacks.values.sumOf { it.size } == 1) return
    Snapshot.withMutableSnapshot {
      topLevelBackStacks.values.forEach { it.clear() }
      topLevelRoute = route.takeIf { it in topLevelBackStacks } ?: startRoute
      activeStack.add(route)
    }
  }

  internal fun push(route: NavKey) {
    when (route) {
      in topLevelBackStacks -> {
        selectTab(route)
      }

      in activeStack -> {
        popTo(route)
      }

      else -> {
        activeStack.add(route)
      }
    }
  }

  internal fun replaceTop(route: NavKey) {
    if (route in activeStack) {
      popTo(route)
      return
    }
    Snapshot.withMutableSnapshot {
      activeStack.removeLastOrNull()
      if (route != topLevelRoute && route in topLevelBackStacks) {
        selectTab(route)
      } else {
        activeStack.add(route)
      }
    }
  }

  internal fun pop() {
    if (activeStack.size > 1) {
      activeStack.removeLastOrNull()
    } else if (topLevelRoute != startRoute) {
      selectTab(startRoute)
    }
  }

  internal fun popTo(route: NavKey) {
    val index = activeStack.lastIndexOf(route)
    if (index in 0 until activeStack.lastIndex) activeStack.subList(index + 1, activeStack.size).clear()
  }

  internal fun removeAll() {
    Snapshot.withMutableSnapshot {
      topLevelBackStacks.values.forEach { it.clear() }
      topLevelRoute = startRoute
    }
  }

  internal fun removeFlow(flowKey: FlowNavKey): Boolean {
    val stacks = topLevelBackStacks.values.filter { flowKey in it }
    if (stacks.isEmpty()) return false
    Snapshot.withMutableSnapshot {
      stacks.forEach { it.removeAll { route -> route == flowKey } }
      if (activeStack.isEmpty()) {
        topLevelRoute = topLevelBackStacks.entries.firstOrNull { it.value.isNotEmpty() }?.key ?: startRoute
      }
    }
    return true
  }

  private fun selectTab(route: NavKey) {
    Snapshot.withMutableSnapshot {
      topLevelRoute = route
      if (activeStack.isEmpty()) activeStack.add(route)
    }
  }

  /**
   * Стеки, которые показывает `NavDisplay`: стартовая вкладка и, если выбрана другая, она сверху.
   * Так «Назад» с корня другой вкладки ведёт на стартовую. На корне стартовой вкладки
   * Navigation3 передаёт Back родительскому контейнеру или системе.
   */
  val stacksInUse: List<NavKey>
    get() = if (topLevelRoute == startRoute) {
      listOf(startRoute)
    } else {
      listOf(startRoute, topLevelRoute)
    }

  /**
   * Записи для `NavDisplay`. Декораторы оборачивают каждую запись, и у каждого стека они свои:
   * - `SaveableStateHolder` хранит `rememberSaveable` экранов, в том числе скрытой вкладки;
   * - `ViewModelStore` даёт записи своё хранилище ViewModel. ViewModel живёт, пока запись в стеке,
   *   и очищается, когда запись из него уходит. Без него ViewModel жили бы в Activity, и экраны
   *   деталей разных городов получили бы один экземпляр;
   */
  @Composable
  fun entries(entryProvider: EntryProvider): List<NavEntry<NavKey>> {
    val decoratedEntries = topLevelBackStacks.mapValues { (_, backStack) ->
      val viewModelStoreProvider = rememberViewModelStoreProvider()
      rememberDecoratedNavEntries(
        backStack = backStack,
        entryProvider = entryProvider,
        entryDecorators = listOf(
          rememberSaveableStateHolderNavEntryDecorator(),
          rememberViewModelStoreNavEntryDecorator(viewModelStoreProvider),
        ),
      )
    }
    return stacksInUse.flatMap { route -> decoratedEntries.getValue(route) }
  }
}

@Composable
fun rememberNavigationState(
  startRoute: NavKey,
  topLevelRoutes: Set<NavKey>,
  entryRoutes: List<NavKey> = listOf(startRoute),
): NavigationState {
  val initialState = remember(startRoute, topLevelRoutes, entryRoutes) {
    NavigationState(
      startRoute = startRoute,
      topLevelRoute = mutableStateOf(startRoute),
      topLevelBackStacks = topLevelRoutes.associateWith { route ->
        if (route == startRoute) NavBackStack() else NavBackStack(route)
      },
    ).apply { entryRoutes.forEach(::push) }
  }
  val topLevelRoute = rememberSerializable(stateSerializer = NavKeySerializer()) {
    mutableStateOf(initialState.topLevelRoute)
  }
  @Suppress("SpreadOperator")
  val topLevelBackStacks = topLevelRoutes.associateWith { route ->
    rememberNavBackStack(*initialState.topLevelBackStacks.getValue(route).toTypedArray())
  }
  return remember(startRoute, topLevelRoutes) {
    NavigationState(
      startRoute = startRoute,
      topLevelRoute = topLevelRoute,
      topLevelBackStacks = topLevelBackStacks
    )
  }
}

internal typealias EntryProvider = (NavKey) -> NavEntry<NavKey>
