package ru.sla.pure.android.feature.main.ui.routing

import ru.sla.pure.android.core.ui.Event

sealed interface FlowEvent : Event {
  data class CharacterDetailsRequested(val characterId: Int) : FlowEvent
  data object CharacterDetailsDismissed : FlowEvent
  data object ProfileRequested : FlowEvent
  data object ProfileDismissed : FlowEvent
  data object LogoutSucceeded : FlowEvent
}
