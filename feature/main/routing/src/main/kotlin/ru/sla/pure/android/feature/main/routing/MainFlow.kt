package ru.sla.pure.android.feature.main.routing

import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.ui.routing.MainFlowEvent

@SingleIn(MainScope::class)
class MainFlow @Inject constructor(
  @ForScope(MainScope::class)
  private val navigator: Navigator
) : Flow {
  override fun onEntry() {
    navigator.setRoot(MainRoute.Main)
  }

  override fun transition(event: Event) {
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
