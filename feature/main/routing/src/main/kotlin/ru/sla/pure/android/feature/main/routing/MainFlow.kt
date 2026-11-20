package ru.sla.pure.android.feature.main.routing

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.ui.routing.FlowEvent

@SingleIn(MainScope::class)
@ContributesBinding(
  MainScope::class,
  binding<@ForScope(MainScope::class) Flow>(),
)
@ContributesBinding(
  MainScope::class,
  binding<FlowEventSink>()
)
class MainFlow @Inject constructor(
  private val navigator: Navigator,
  @ForScope(AppFlowScope::class)
  private val appEvents: FlowEventSink,
) : Flow {
  override fun onEntry(): List<NavKey> {
    return listOf(MainRoute.Main)
  }

  override fun transition(event: Event) {
    when (event) {
      is FlowEvent.CharacterDetailsRequested -> {
        navigator.push(MainRoute.CharacterDetails(event.characterId))
      }
      is FlowEvent.CharacterDetailsDismissed -> {
        navigator.pop()
      }
      FlowEvent.ProfileRequested -> {
        navigator.push(MainRoute.Profile)
      }
      FlowEvent.ProfileDismissed -> {
        navigator.pop()
      }
      FlowEvent.LogoutSucceeded -> {
        appEvents.sendEvent(event)
      }
    }
  }

  companion object {
    val navKey = FlowNavKey("main")
  }
}
