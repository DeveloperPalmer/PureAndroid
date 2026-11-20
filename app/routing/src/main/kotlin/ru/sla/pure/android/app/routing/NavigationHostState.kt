package ru.sla.pure.android.app.routing

import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.FlowNavKey

/** Подключённые стеки и отложенные изменения корня и флоу одного хоста. */
class NavigationHostState(internal val root: FlowNavKey) {
  private val containers = mutableMapOf<FlowNavKey, NavigationState>()
  private val pendingFlowRemovals = mutableMapOf<FlowNavKey, MutableSet<FlowNavKey>>()
  private var rootReplacement: RootReplacement? = null

  val isRootAttached: Boolean
    get() = root in containers

  val currentRoute: NavKey?
    get() = containers[root]?.currentRoute

  private val activeFlowKey: FlowNavKey?
    get() {
      var flowKey = root
      while (true) {
        val state = containers[flowKey] ?: return null
        flowKey = state.currentRoute as? FlowNavKey ?: return flowKey
      }
    }

  internal val activeState: NavigationState?
    get() = activeFlowKey?.let(containers::get)

  internal fun replaceRoot(route: NavKey?) {
    rootReplacement = RootReplacement(route)
    if (route == null) {
      pendingFlowRemovals.clear()
      containers[root]?.removeAll()
    } else {
      pendingFlowRemovals.remove(root)
      containers[root]?.setRoot(route)
    }
  }

  internal fun removeFlow(flowKey: FlowNavKey) {
    if (rootReplacement?.route == flowKey) rootReplacement = null
    if (!isRootAttached) pendingFlowRemovals.getOrPut(root) { mutableSetOf() }
    // Повторяем удаление при подключении, если Compose восстановит прежний снимок стека.
    pendingFlowRemovals.values.forEach { it.add(flowKey) }
    containers.forEach { (parentKey, state) ->
      if (state.removeFlow(flowKey)) {
        pendingFlowRemovals.getOrPut(parentKey) { mutableSetOf() }.add(flowKey)
      }
    }
  }

  internal fun attach(flowKey: FlowNavKey, state: NavigationState) {
    containers[flowKey] = state
    if (flowKey == root) {
      // Снимок Activity мог быть сохранён до смены корня.
      rootReplacement?.let { replacement ->
        if (state.firstRoute != replacement.route) {
          replacement.route?.let(state::setRoot) ?: state.removeAll()
        }
      }
    }
    pendingFlowRemovals.remove(flowKey)?.forEach { state.removeFlow(it) }
  }

  internal fun detach(flowKey: FlowNavKey, state: NavigationState) {
    if (containers[flowKey] === state) {
      containers.remove(flowKey)
      pendingFlowRemovals.getOrPut(flowKey) { mutableSetOf() }
    }
  }

  internal fun updateActiveStack(route: NavKey, update: (NavigationState) -> Unit) {
    val flowKey = activeFlowKey ?: return
    val state = containers.getValue(flowKey)
    if (route is FlowNavKey) {
      pendingFlowRemovals[flowKey]?.remove(route)
    }
    val previousRoot = state.firstRoute
    update(state)
    if (flowKey == root && state.firstRoute != previousRoot) {
      rootReplacement = RootReplacement(state.firstRoute)
    }
  }

  private data class RootReplacement(val route: NavKey?)
}
