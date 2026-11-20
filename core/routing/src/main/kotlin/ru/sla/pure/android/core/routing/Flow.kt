package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink

interface Flow : FlowEventSink {
  /** Начальные записи стека в порядке от корня к верхней записи. */
  fun onEntry(): List<NavKey>

  fun transition(event: Event)

  override fun sendEvent(event: Event) = transition(event)
}
