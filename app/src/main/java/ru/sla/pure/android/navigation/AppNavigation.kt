package ru.sla.pure.android.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.sla.pure.android.feature.cities.routing.CitiesRoute
import ru.sla.pure.android.feature.cities.routing.CityDetailsRoute
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen
import ru.sla.pure.android.feature.map.routing.MapRoute
import ru.sla.pure.android.feature.map.ui.screen.map.MapScreen

/**
 * Навигация приложения: один `NavDisplay` на все экраны. В Navigation 3 стеком владеет само
 * приложение: это обычные списки ключей в [NavigationState], а переходы делает [Navigator].
 * Нижнюю панель декоратор сцен добавляет корням вкладок, детали города открываются без неё.
 */
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = TopLevelDestination.entries.first().route,
        topLevelRoutes = TOP_LEVEL_ROUTES,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val entryProvider = entryProvider<NavKey> {
        entry<CitiesRoute>(metadata = NavigationBarSceneDecoratorStrategy.showNavigationBar()) {
            CitiesScreen(
                onCityClick = { cityId -> navigator.navigateTo(CityDetailsRoute(cityId)) }
            )
        }
        entry<CityDetailsRoute> { route ->
            CityDetailsScreen(
                cityId = route.cityId,
                onBack = navigator::navigateBack
            )
        }
        entry<MapRoute>(metadata = NavigationBarSceneDecoratorStrategy.showNavigationBar(),) {
            MapScreen()
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
        )
    }
}

private val TOP_LEVEL_ROUTES: Set<NavKey> = TopLevelDestination.entries.mapTo(LinkedHashSet()) { it.route }
