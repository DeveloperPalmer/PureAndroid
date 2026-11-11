package ru.sla.pure.android.feature.app.routing

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.feature.app.ui.routing.FlowEvent
import ru.sla.pure.android.feature.main.routing.MainRoute

@SingleIn(AppFlowScope::class)
class AppFlow @Inject constructor(private val navigator: Navigator) : Flow {
  override fun onEntry() {
    navigator.setRoot(AppRoute.InitialResolve)
  }

  override fun transition(event: Event) {
    when (event) {
      FlowEvent.ActiveSessionResolved -> {
        navigator.replaceRoot(MainRoute.Main)
      }
      FlowEvent.InactiveSessionResolved -> {
        // TODO: start login flow instead of main flow
        navigator.replaceRoot(MainRoute.Main)
      }
    }
  }
}
