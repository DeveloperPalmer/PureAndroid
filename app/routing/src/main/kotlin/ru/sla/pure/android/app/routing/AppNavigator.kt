package ru.sla.pure.android.app.routing

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.routing.Navigator

@SingleIn(ForegroundScope::class)
@ContributesBinding(ForegroundScope::class)
class AppNavigator @Inject constructor(private val host: NavigationHostState) : Navigator {
  override fun setRoot(route: NavKey) {
    host.replaceRoot(route)
  }

  override fun push(route: NavKey) {
    host.updateActiveStack(route) { it.push(route) }
  }

  override fun replaceTop(route: NavKey) {
    host.updateActiveStack(route) { it.replaceTop(route) }
  }

  override fun pop() {
    host.activeState?.pop()
  }

  override fun popTo(route: NavKey) {
    host.activeState?.popTo(route)
  }

  override fun removeAll() {
    host.replaceRoot(null)
  }

  override fun removeAll(flowScope: String) {
    val flowKey = FlowNavKey(flowScope)
    if (flowKey == host.root) {
      removeAll()
    } else {
      host.removeFlow(flowKey)
    }
  }
}
