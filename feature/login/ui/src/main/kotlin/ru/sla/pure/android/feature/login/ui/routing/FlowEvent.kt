package ru.sla.pure.android.feature.login.ui.routing

import ru.sla.pure.android.core.ui.Event

sealed interface FlowEvent : Event {
  data object LoginSucceeded : FlowEvent
}
