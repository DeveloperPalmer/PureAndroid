package ru.sla.pure.android.feature.app.routing

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.app.ui.routing.FlowEvent
import ru.sla.pure.android.feature.login.routing.LoginFlow
import ru.sla.pure.android.feature.main.routing.MainFlow
import ru.sla.pure.android.feature.login.ui.routing.FlowEvent as LoginFlowEvent
import ru.sla.pure.android.feature.main.ui.routing.FlowEvent as MainFlowEvent

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
  override fun onEntry(): List<NavKey> {
    return listOf(AppRoute.InitialResolve)
  }

  override fun transition(event: Event) {
    when (event) {
      is FlowEvent.SessionResolved -> {
        navigator.setRoot(selectRoot(event.session))
      }
      is LoginFlowEvent.LoginSucceeded -> {
        navigator.setRoot(MainFlow.navKey)
      }
      is MainFlowEvent.LogoutSucceeded -> {
        navigator.setRoot(LoginFlow.navKey)
      }
    }
  }

  companion object {
    val navKey = FlowNavKey("appFlow")
  }
}

internal fun selectRoot(session: AuthSessionState): FlowNavKey = when (session) {
  AuthSessionState.Active -> MainFlow.navKey
  AuthSessionState.Inactive -> LoginFlow.navKey
}
