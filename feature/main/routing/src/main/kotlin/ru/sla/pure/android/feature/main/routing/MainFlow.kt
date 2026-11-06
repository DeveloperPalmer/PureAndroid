package ru.sla.pure.android.feature.main.routing

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent

@SingleIn(AppFlowScope::class)
class MainFlow @Inject constructor(
  private val navigator: Navigator
) : FlowEventSink {
  override fun sendEvent(event: Event) {
    when (event) {
      is MainFlowEvent.CharacterDetailsRequested -> {
        navigator.navigateTo(MainRoute.CharacterDetails(event.characterId))
      }
      is MainFlowEvent.CharacterDetailsDismissed -> {
        navigator.navigateBack()
      }
    }
  }
}
