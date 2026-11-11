package ru.sla.pure.android.feature.app.ui.routing

import ru.sla.pure.android.core.ui.Event

sealed interface FlowEvent : Event {
  data object ActiveSessionResolved : FlowEvent
  data object InactiveSessionResolved : FlowEvent
}
