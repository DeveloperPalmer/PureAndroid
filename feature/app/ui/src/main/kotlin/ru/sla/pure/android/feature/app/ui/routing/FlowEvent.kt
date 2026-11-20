package ru.sla.pure.android.feature.app.ui.routing

import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.ui.Event

sealed interface FlowEvent : Event {
  data class SessionResolved(val session: AuthSessionState) : FlowEvent
}
