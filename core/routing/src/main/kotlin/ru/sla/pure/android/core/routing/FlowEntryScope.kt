package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata as navMetadata

/** Регистрирует контейнер флоу в родительском стеке по маршруту [initial]. */
fun <C : FlowComponent> EntryProviderScope<NavKey>.flow(
  initial: NavKey,
  component: (Navigator) -> C,
  tabs: List<NavigationTab> = emptyList(),
  metadata: Map<String, Any> = emptyMap(),
  entries: EntryProviderScope<NavKey>.(C) -> Unit,
) {
  entry(
    key = initial,
    metadata = metadata + navMetadata { put(FlowContainerKey, true) }
  ) {
    LocalFlowHost.current.Content(tabs, component, entries)
  }
}
