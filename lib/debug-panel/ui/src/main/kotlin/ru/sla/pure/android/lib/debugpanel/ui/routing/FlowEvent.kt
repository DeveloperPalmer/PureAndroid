package ru.sla.pure.android.lib.debugpanel.ui.routing

import ru.sla.pure.android.core.ui.Event

sealed interface FlowEvent : Event {
  data object DebugPanelDismissed : FlowEvent
}
