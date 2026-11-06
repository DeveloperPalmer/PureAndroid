package ru.sla.pure.android.feature.app

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.feature.app.entity.TopLevelDestination

@Composable
fun AppFlowHost(
  navigator: AppNavigator,
  entryProviderInstallers: Set<EntryProviderInstaller>,
  modifier: Modifier = Modifier,
) {
  val navigationState = rememberNavigationState(
    startRoute = TopLevelDestination.entries.first().route,
    topLevelRoutes = TOP_LEVEL_ROUTES,
  )

  DisposableEffect(navigator, navigationState) {
    navigator.attach(navigationState)
    onDispose { navigator.detach(navigationState) }
  }

  val entryProvider = entryProvider {
    entryProviderInstallers.forEach { installer ->
      with(installer) { install() }
    }
  }

  // SharedTransitionLayout нужен декоратору: через общий элемент панель стоит на месте,
  // пока сцены с ней сменяют друг друга
  SharedTransitionLayout(modifier) {
    val navigationBarDecorator = rememberNavigationBarSceneDecoratorStrategy<NavKey>(
      sharedTransitionScope = this,
      navigationBar = {
        AppNavigationBar(
          selectedRoute = navigationState.topLevelRoute,
          onSelect = navigator::navigateTo,
        )
      }
    )
    NavDisplay(
      entries = navigationState.entries(entryProvider),
      onBack = navigator::navigateBack,
      sceneDecoratorStrategies = listOf(navigationBarDecorator),
      sharedTransitionScope = this,
      transitionSpec = SlideTransitions.forward(),
      popTransitionSpec = SlideTransitions.back(),
      predictivePopTransitionSpec = SlideTransitions.predictiveBack(),
    )
  }
}

@Composable
private fun AppNavigationBar(
  selectedRoute: NavKey,
  onSelect: (NavKey) -> Unit,
) {
  NavigationBar {
    TopLevelDestination.entries.forEach { destination ->
      NavigationBarItem(
        selected = destination.route == selectedRoute,
        onClick = { onSelect(destination.route) },
        icon = { Icon(painterResource(destination.icon), contentDescription = null) },
        label = { Text(stringResource(destination.label)) },
      )
    }
  }
}

private val TOP_LEVEL_ROUTES: Set<NavKey> = TopLevelDestination.entries.mapTo(LinkedHashSet()) { it.route }
