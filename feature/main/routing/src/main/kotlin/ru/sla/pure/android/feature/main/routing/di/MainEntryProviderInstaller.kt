package ru.sla.pure.android.feature.main.routing.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.main.routing.MainRoute
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsViewModel
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen

@ContributesIntoSet(AppFlowScope::class)
class MainEntryProviderInstaller @Inject constructor(
  private val componentFactory: MainFlowComponent.Factory,
) : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    flow(
      root = MainRoute.Main,
      component = componentFactory::create,
    ) {
      entry<MainRoute.Main>(metadata = showNavigationBar()) {
        MainScreen(viewModel = metroViewModel())
      }
      entry<MainRoute.CharacterDetails> { route ->
        val viewModel = assistedMetroViewModel<CharacterDetailsViewModel, CharacterDetailsViewModel.Factory> {
          create(route.characterId)
        }
        CharacterDetailsScreen(viewModel = viewModel)
      }
    }
  }
}
