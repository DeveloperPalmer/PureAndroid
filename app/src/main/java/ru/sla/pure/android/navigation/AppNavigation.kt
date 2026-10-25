package ru.sla.pure.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import ru.sla.pure.android.feature.cities.routing.CitiesRoute
import ru.sla.pure.android.feature.cities.routing.CityDetailsRoute
import ru.sla.pure.android.feature.cities.ui.screen.cities.CitiesScreen
import ru.sla.pure.android.feature.cities.ui.screen.citydetails.CityDetailsScreen

/**
 * Навигация приложения. В Navigation 3 стеком владеет само приложение: это обычный список ключей,
 * переход — `add`, назад — `removeLastOrNull`. `rememberNavBackStack` сохраняет его через
 * сериализацию ключей, поэтому стек переживает пересоздание Activity и смерть процесса.
 * `NavDisplay` показывает верхний ключ стека, а `entryProvider` сопоставляет ключу экран.
 */
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(CitiesRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<CitiesRoute> {
                CitiesScreen(
                    onCityClick = { cityId -> backStack.add(CityDetailsRoute(cityId)) }
                )
            }
            entry<CityDetailsRoute> { route ->
                CityDetailsScreen(
                    cityId = route.cityId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
