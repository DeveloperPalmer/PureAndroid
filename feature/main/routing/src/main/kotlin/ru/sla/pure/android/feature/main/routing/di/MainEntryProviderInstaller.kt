package ru.sla.pure.android.feature.main.routing.di

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.NavigationTab
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.main.routing.MainRoute
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsScreen
import ru.sla.pure.android.feature.main.ui.screen.characterdetails.CharacterDetailsViewModel
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import ru.sla.pure.android.feature.main.ui.screen.profile.ProfileScreen
import ru.sla.pure.android.feature.map.routing.MapEntryProviderInstaller
import ru.sla.pure.android.feature.map.routing.MapRoute

class MainEntryProviderInstaller(
  private val componentFactory: MainFlowComponent.Factory,
) : EntryProviderInstaller {
  override fun EntryProviderScope<NavKey>.install() {
    flow(
      tabs = tabs,
      initial = MainRoute.Main,
      component = componentFactory::createMainFlowComponent,
      metadata = NavDisplay.transitionSpec { EnterTransition.None togetherWith ExitTransition.None },
    ) { _ ->
      with(MapEntryProviderInstaller) { install() }

      entry<MainRoute.Main>(metadata = showNavigationBar()) {
        MainScreen(viewModel = metroViewModel())
      }

      entry<MainRoute.Profile> {
        ProfileScreen(viewModel = metroViewModel())
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

private val tabs = listOf(
  NavigationTab(MainRoute.Main, R.drawable.ic_list_24, R.string.tab_main),
  NavigationTab(MapRoute.Map, R.drawable.ic_map_24, R.string.tab_map),
)
