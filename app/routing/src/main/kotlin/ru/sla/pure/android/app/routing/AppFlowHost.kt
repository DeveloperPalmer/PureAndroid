package ru.sla.pure.android.app.routing

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.FlowComponentViewModel
import ru.sla.pure.android.core.routing.FlowHost
import ru.sla.pure.android.core.routing.LocalFlowHost
import ru.sla.pure.android.core.routing.NavigationTab
import ru.sla.pure.android.core.routing.Navigator

@Composable
fun AppFlowHost(
  flowRoute: NavKey,
  navigator: AppNavigator,
  entryProviderInstallers: Set<EntryProviderInstaller>,
  modifier: Modifier = Modifier,
) = key(flowRoute) {
  val state = rememberNavigationState(
    startRoute = flowRoute,
    topLevelRoutes = setOf(flowRoute)
  )
  val entryProvider = remember(entryProviderInstallers) {
    entryProvider<NavKey> {
      entryProviderInstallers.forEach { installer -> with(installer) { install() } }
    }
  }
  val appFlowContainer = remember(navigator) { AppFlowContainer(navigator) }
  val hostNavigator = remember { AppNavigator() }
  CompositionLocalProvider(LocalFlowHost provides appFlowContainer) {
    NavigationContent(
      modifier = modifier,
      state = state,
      navigator = hostNavigator,
      entryProvider = entryProvider,
    )
  }
}

private val nestedFlowContainer = AppFlowContainer()

private class AppFlowContainer(
  private val navigator: AppNavigator? = null,
) : FlowHost {
  @Composable
  override fun <C : FlowComponent> Content(
    tabs: List<NavigationTab>,
    component: (Navigator) -> C,
    entries: EntryProviderScope<NavKey>.(C) -> Unit,
  ) {
    val holder = viewModel<FlowContainerHolder<C>>(
      factory = viewModelFactory {
        initializer { FlowContainerHolder(navigator ?: AppNavigator(), component) }
      },
    )
    val startRoute = holder.navigator.initialRoute
    val topLevelRoutes = if (tabs.isEmpty()) setOf(startRoute) else tabs.mapTo(linkedSetOf()) { it.route }
    check(startRoute in topLevelRoutes) { "Flow root must be one of its tabs" }
    val state = rememberNavigationState(startRoute, topLevelRoutes)
    val entryProvider = remember(holder, entries) {
      entryProvider<NavKey> { entries(holder.component) }
    }
    CompositionLocalProvider(
      LocalFlowHost provides nestedFlowContainer,
      LocalMetroViewModelFactory provides holder.viewModelFactory,
    ) {
      NavigationContent(
        state = state,
        navigator = holder.navigator,
        entryProvider = entryProvider,
        tabs = tabs,
      )
    }
  }
}

@Composable
private fun NavigationContent(
  state: NavigationState,
  navigator: AppNavigator,
  entryProvider: EntryProvider,
  modifier: Modifier = Modifier,
  tabs: List<NavigationTab> = emptyList(),
) {
  DisposableEffect(navigator, state) {
    navigator.attach(state)
    onDispose { navigator.detach(state) }
  }
  SharedTransitionLayout(modifier) {
    val navigationBarDecorator = rememberNavigationBarSceneDecoratorStrategy<NavKey>(
      sharedTransitionScope = this,
      navigationBar = {
        if (tabs.isNotEmpty()) {
          AppNavigationBar(tabs, state.topLevelRoute, navigator::navigateTo)
        }
      },
    )
    NavDisplay(
      entries = state.entries(entryProvider),
      onBack = navigator::navigateBack,
      sceneDecoratorStrategies = listOf(navigationBarDecorator),
      sharedTransitionScope = this,
      transitionSpec = SlideTransitions.forward(),
      popTransitionSpec = SlideTransitions.back(),
      predictivePopTransitionSpec = SlideTransitions.predictiveBack(),
    )
  }
}

private class FlowContainerHolder<C : FlowComponent>(
  val navigator: AppNavigator,
  factory: (Navigator) -> C,
) : FlowComponentViewModel<C>(factory(navigator))
