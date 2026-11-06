package ru.sla.pure.android.feature.app.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
