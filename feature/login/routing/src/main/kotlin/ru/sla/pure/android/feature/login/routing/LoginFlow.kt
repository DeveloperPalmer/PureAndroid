package ru.sla.pure.android.feature.login.routing

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
import ru.sla.pure.android.feature.login.domain.di.LoginScope
import ru.sla.pure.android.feature.login.ui.routing.FlowEvent

@SingleIn(LoginScope::class)
@ContributesBinding(
  LoginScope::class,
  binding<@ForScope(LoginScope::class) Flow>(),
)
@ContributesBinding(
  LoginScope::class,
  binding<@ForScope(LoginScope::class) FlowEventSink>(),
)
class LoginFlow @Inject constructor(
  @ForScope(LoginScope::class)
  private val navigator: Navigator,
  @ForScope(AppFlowScope::class)
  private val appEvents: FlowEventSink,
) : Flow {
  override fun onEntry() {
    navigator.setRoot(LoginRoute.Credentials)
  }

  override fun transition(event: Event) {
    when (event) {
      FlowEvent.LoginSucceeded -> {
        appEvents.sendEvent(FlowEvent.LoginSucceeded)
      }
    }
  }
}
