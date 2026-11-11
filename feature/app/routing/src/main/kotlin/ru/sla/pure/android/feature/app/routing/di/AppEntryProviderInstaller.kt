package ru.sla.pure.android.feature.app.routing.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.feature.app.routing.AppRoute
import ru.sla.pure.android.feature.app.ui.screen.initialresolve.InitialResolveScreen
import ru.sla.pure.android.feature.login.routing.di.LoginEntryProviderInstaller
import ru.sla.pure.android.feature.main.routing.di.MainEntryProviderInstaller

@ContributesIntoSet(ForegroundScope::class)
class AppEntryProviderInstaller @Inject constructor(
  private val componentFactory: AppFlowComponent.Factory,
) : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    flow(
      initial = AppRoute.InitialResolve,
      component = componentFactory::create,
    ) { appComponent ->
      with(LoginEntryProviderInstaller(appComponent.loginFlowFactory())) { install() }
      with(MainEntryProviderInstaller(appComponent.mainFlowFactory())) { install() }

      entry<AppRoute.InitialResolve> {
        InitialResolveScreen(viewModel = metroViewModel())
      }
    }
  }
}
