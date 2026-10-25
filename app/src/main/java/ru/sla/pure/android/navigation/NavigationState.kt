package ru.sla.pure.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
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
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

    /**
     * Стеки, которые показывает `NavDisplay`: стартовая вкладка и, если выбрана другая, она сверху.
     * Так «Назад» с корня любой вкладки ведёт на стартовую, а с неё — из приложения
     * (схема «exit through home»).
     */
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
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
    val backStacks = topLevelRoutes.associateWith { route -> rememberNavBackStack(route) }
    return remember(startRoute, topLevelRoutes) {
        NavigationState(startRoute, topLevelRoute, backStacks)
    }
}

/**
 * Записи для `NavDisplay`. У каждого стека свой декоратор `SaveableStateHolder`: состояние экранов
 * скрытой вкладки (`rememberSaveable`) сохраняется, пока её стек не в работе.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): List<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, backStack) ->
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
            entryProvider = entryProvider,
        )
    }
    return stacksInUse.flatMap { route -> decoratedEntries.getValue(route) }
}
