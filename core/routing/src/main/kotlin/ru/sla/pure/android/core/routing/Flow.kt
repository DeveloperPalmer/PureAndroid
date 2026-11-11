package ru.sla.pure.android.core.routing

import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink

interface Flow : FlowEventSink {
  fun onEntry()

  fun transition(event: Event)

  override fun sendEvent(event: Event) = transition(event)
}
