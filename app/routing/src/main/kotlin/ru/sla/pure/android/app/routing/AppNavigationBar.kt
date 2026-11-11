package ru.sla.pure.android.app.routing

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.NavigationTab

@Composable
internal fun AppNavigationBar(
  tabs: List<NavigationTab>,
  selectedRoute: NavKey,
  onSelect: (NavKey) -> Unit,
) {
  NavigationBar {
    tabs.forEach { tab ->
      NavigationBarItem(
        selected = tab.route == selectedRoute,
        onClick = { onSelect(tab.route) },
        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
        label = { Text(stringResource(tab.label)) },
      )
    }
  }
}
