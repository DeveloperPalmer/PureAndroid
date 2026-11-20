package ru.sla.pure.android.app.routing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.FlowComponentViewModel
import ru.sla.pure.android.core.routing.FlowHost
import ru.sla.pure.android.core.routing.FlowNavKey
import ru.sla.pure.android.core.routing.LocalFlowHost
import ru.sla.pure.android.core.routing.NavigationTab
import ru.sla.pure.android.uikit.theme.AppTheme

@Composable
fun AppFlowHost(
  hostState: NavigationHostState,
  entryProviderInstallers: Set<EntryProviderInstaller>,
  modifier: Modifier = Modifier,
) = key(hostState.root) {
  val state = rememberNavigationState(
    startRoute = hostState.root,
    topLevelRoutes = setOf(hostState.root)
  )
  val entryProvider = remember(entryProviderInstallers) {
    entryProvider<NavKey> {
      entryProviderInstallers.forEach { installer -> with(installer) { install() } }
    }
  }
  val appFlowContainer = remember(hostState) { AppFlowContainer(hostState) }
  CompositionLocalProvider(LocalFlowHost provides appFlowContainer) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(AppTheme.colors.backgroundPrimary),
    ) {
      NavigationContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        entryProvider = entryProvider,
      )
    }
  }
}

private class AppFlowContainer(private val hostState: NavigationHostState) : FlowHost {
  @Composable
  override fun <C : FlowComponent> Content(
    flowKey: FlowNavKey,
    tabs: List<NavigationTab>,
    component: () -> C,
    entries: EntryProviderScope<NavKey>.(C) -> Unit,
  ) {
    val holder = viewModel<FlowComponentViewModel<C>>(
      factory = viewModelFactory {
        initializer { FlowComponentViewModel(component()) }
      },
    )
    val tabRoutes = tabs.mapTo(linkedSetOf()) { it.route }
    val firstRoute = holder.entryRoutes.firstOrNull()
    val startRoute = if (tabRoutes.isEmpty()) {
      firstRoute ?: flowKey
    } else {
      firstRoute?.takeIf { it in tabRoutes } ?: tabRoutes.first()
    }
    val state = rememberNavigationState(startRoute, tabRoutes.ifEmpty { setOf(startRoute) }, holder.entryRoutes)
    val entryProvider = remember(holder, entries) {
      entryProvider<NavKey> { entries(holder.component) }
    }
    DisposableEffect(hostState, flowKey, state) {
      hostState.attach(flowKey, state)
      onDispose { hostState.detach(flowKey, state) }
    }
    CompositionLocalProvider(LocalMetroViewModelFactory provides holder.viewModelFactory) {
      NavigationContent(
        state = state,
        entryProvider = entryProvider,
        tabs = tabs,
      )
    }
  }
}
