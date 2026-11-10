package ru.sla.pure.android.core.routing

import androidx.lifecycle.ViewModel
import leakcanary.AppWatcher

internal fun watchClearedFlow(holder: ViewModel, component: FlowComponent, flowKey: String) {
  if (!AppWatcher.isInstalled) {
    return
  }
  AppWatcher.objectWatcher.expectWeaklyReachable(holder, "Flow graph holder cleared for $flowKey")
  AppWatcher.objectWatcher.expectWeaklyReachable(component, "Flow component cleared for $flowKey")
}
