package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata as navMetadata

/** Регистрирует контейнер флоу в родительском стеке по [key]. */
fun <C : FlowComponent> EntryProviderScope<NavKey>.flow(
  key: FlowNavKey,
  component: () -> C,
  tabs: List<NavigationTab> = emptyList(),
  metadata: Map<String, Any> = emptyMap(),
  entries: EntryProviderScope<NavKey>.(C) -> Unit,
) {
  entry(
    key = key,
    metadata = metadata + navMetadata { put(FlowContainerKey, true) }
  ) {
    LocalFlowHost.current.Content(key, tabs, component, entries)
  }
}
