package ru.sla.pure.android.feature.app.routing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.app.ui.routing.FlowEvent
import ru.sla.pure.android.feature.login.routing.LoginRoute
import ru.sla.pure.android.feature.main.routing.MainRoute
import ru.sla.pure.android.feature.login.ui.routing.FlowEvent as LoginFlowEvent

@SingleIn(AppFlowScope::class)
@ContributesBinding(
  AppFlowScope::class,
  binding<@ForScope(AppFlowScope::class) Flow>(),
)
@ContributesBinding(
  AppFlowScope::class,
  binding<@ForScope(AppFlowScope::class) FlowEventSink>(),
)
class AppFlow @Inject constructor(private val navigator: Navigator) : Flow {
  override fun onEntry() {
    navigator.setRoot(AppRoute.InitialResolve)
  }

  override fun transition(event: Event) {
    when (event) {
      is LoginFlowEvent.LoginSucceeded,
      is FlowEvent.ActiveSessionResolved -> {
        navigator.replaceRoot(MainRoute.Main)
      }
      is FlowEvent.InactiveSessionResolved -> {
        navigator.replaceRoot(LoginRoute.Credentials)
      }
    }
  }
}
