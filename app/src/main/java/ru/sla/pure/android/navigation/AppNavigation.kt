package ru.sla.pure.android.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.sla.pure.android.core.routing.EntryProviderInstaller

/**
 * Навигация приложения: один `NavDisplay` на все экраны. В Navigation 3 стеком владеет само
 * приложение: это обычные списки ключей в [NavigationState], а переходы делает [AppNavigator].
 * Экраны приходят из модулей routing фич через [entryProviderInstallers], и они же по колбэкам
 * экранов вызывают переходы [AppNavigator]. Модули ui приложению не видны.
 * Нижнюю панель декоратор сцен добавляет записям с `showNavigationBar()`.
 */
@Composable
fun AppNavigation(
  entryProviderInstallers: Set<EntryProviderInstaller>,
  modifier: Modifier = Modifier,
) {
  val navigationState = rememberNavigationState(
    startRoute = TopLevelDestination.entries.first().route,
    topLevelRoutes = TOP_LEVEL_ROUTES,
  )
  val navigator = remember(navigationState) { AppNavigator(navigationState) }

  val entryProvider = entryProvider<NavKey> {
    entryProviderInstallers.forEach { installer ->
      with(installer) { install(navigator) }
    }
  }

  // SharedTransitionLayout нужен декоратору: через общий элемент панель стоит на месте,
  // пока сцены с ней сменяют друг друга
  SharedTransitionLayout(modifier) {
    val navigationBarDecorator = rememberNavigationBarSceneDecoratorStrategy<NavKey>(
      sharedTransitionScope = this,
    ) {
      AppNavigationBar(
        selectedRoute = navigationState.topLevelRoute,
        onSelect = navigator::navigateTo,
      )
    }
    NavDisplay(
      entries = navigationState.toEntries(entryProvider),
      onBack = navigator::navigateBack,
      sceneDecoratorStrategies = listOf(navigationBarDecorator),
      sharedTransitionScope = this,
      transitionSpec = SlideTransitions.forward(),
      popTransitionSpec = SlideTransitions.back(),
      predictivePopTransitionSpec = SlideTransitions.predictiveBack(),
    )
  }
}

private val TOP_LEVEL_ROUTES: Set<NavKey> = TopLevelDestination.entries.mapTo(LinkedHashSet()) { it.route }
