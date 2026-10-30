package ru.sla.pure.android.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.feature.main.routing.MainRoute
import ru.sla.pure.android.feature.map.routing.MapRoute

/** Вкладки нижней панели. Первая — стартовая */
enum class TopLevelDestination(
  val route: NavKey,
  @param:DrawableRes
  val icon: Int,
  @param:StringRes
  val label: Int,
) {
  Main(
    route = MainRoute.Main,
    icon = R.drawable.ic_list_24,
    label = R.string.tab_main
  ),
  Map(
    route = MapRoute.Map,
    icon = R.drawable.ic_map_24,
    label = R.string.tab_map
  ),
}

@Composable
fun AppNavigationBar(
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
