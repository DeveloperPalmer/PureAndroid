package ru.sla.pure.android.feature.main.ui.routing

import ru.sla.pure.android.core.ui.Event

sealed interface MainFlowEvent : Event {
  data class CharacterDetailsRequested(val characterId: Int) : MainFlowEvent
  data object CharacterDetailsDismissed : MainFlowEvent
}
