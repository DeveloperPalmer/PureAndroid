package ru.sla.pure.android.app.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer

/**
 * Состояние вкладок: у каждой вкладки свой стек, и они живут параллельно. Переключение вкладки
 * меняет только [topLevelRoute], стеки остаются как были. Каждый стек и текущая вкладка сохраняются
 * через сериализацию ключей, поэтому переживают пересоздание Activity и смерть процесса.
 */
class NavigationState(
  val startRoute: NavKey,
  topLevelRoute: MutableState<NavKey>,
  val topLevelBackStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
  var topLevelRoute: NavKey by topLevelRoute

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
): NavigationState {
  val topLevelRoute = rememberSerializable(stateSerializer = NavKeySerializer()) {
    mutableStateOf(startRoute)
  }
  val topLevelBackStacks = topLevelRoutes.associateWith { route ->
    rememberNavBackStack(route)
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
