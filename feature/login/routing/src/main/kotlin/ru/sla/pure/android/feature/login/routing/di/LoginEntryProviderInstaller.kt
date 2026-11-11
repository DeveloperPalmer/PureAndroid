package ru.sla.pure.android.feature.login.routing.di

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.feature.login.routing.LoginRoute
import ru.sla.pure.android.feature.login.ui.screen.credentials.CredentialsScreen

class LoginEntryProviderInstaller(
  private val componentFactory: LoginFlowComponent.Factory
) : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    flow(
      initial = LoginRoute.Credentials,
      component = componentFactory::createLoginFlowComponent,
      metadata = NavDisplay.transitionSpec { EnterTransition.None togetherWith ExitTransition.None },
    ) { _ ->
      entry<LoginRoute.Credentials> {
        CredentialsScreen(viewModel = metroViewModel())
      }
    }
  }
}
