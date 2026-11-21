package ru.sla.pure.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import ru.sla.pure.android.app.routing.AppFlowHost
import ru.sla.pure.android.app.routing.NavigationHostState
import ru.sla.pure.android.core.routing.deeplink.PendingDeepLink
import ru.sla.pure.android.feature.app.routing.AppRoute
import ru.sla.pure.android.lib.debugpanel.routing.DebugPanelFlow
import ru.sla.pure.android.uikit.theme.AppTheme
import ru.sla.pure.android.uikit.window.setNavigationBarColorCompat

/**
 * Единственная Activity: запускает корневой флоу приложения.
 */
class MainActivity : ComponentActivity() {
  private val appFlowViewModel by viewModels<AppFlowViewModel> {
    viewModelFactory {
      initializer {
        val app = application as PureApplication
        val pendingDeepLink = PendingDeepLink(
          savedStateHandle = createSavedStateHandle(),
          restoreLegacyDestination = ::deepLinkBackStack
        )
        val foregroundComponent = app.foregroundFactory.create(
          activity = this@MainActivity,
          pendingDeepLink = pendingDeepLink
        )
        AppFlowViewModel(foregroundComponent)
      }
    }
  }

  private val debugPanelAdapter by lazy {
    if (BuildConfig.DEBUG) {
      appFlowViewModel.foregroundComponent.debugPanelAdapter()
    } else {
      null
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      handleDeepLink(intent)
    }
    enableEdgeToEdge()
    window.setNavigationBarColorCompat(Color.TRANSPARENT, contrastEnforced = false)

    val foregroundComponent = appFlowViewModel.foregroundComponent
    val hostState = foregroundComponent.navigationHostState()
    val entryProviderInstallers = foregroundComponent.entryProviderInstallers()

    splashScreen.setKeepOnScreenCondition {
      val route = hostState.currentRoute
      route == null || route == AppRoute.InitialResolve
    }

    setContent {
      AppTheme {
        val showDebugPanel = debugPanelAdapter?.isOpen?.collectAsState()?.value == true
        val appBackDispatcher = rememberNavigationEventDispatcherOwner(enabled = !showDebugPanel)
        Box {
          CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides appBackDispatcher) {
            AppFlowHost(
              hostState = hostState,
              entryProviderInstallers = entryProviderInstallers,
              modifier = if (showDebugPanel) Modifier.clearAndSetSemantics { } else Modifier
            )
          }
          if (showDebugPanel) {
            AppFlowHost(
              hostState = remember { NavigationHostState(DebugPanelFlow.navKey) },
              entryProviderInstallers = entryProviderInstallers
            )
          }
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    debugPanelAdapter?.start(this)
  }

  override fun onStop() {
    debugPanelAdapter?.stop(this)
    super.onStop()
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleDeepLink(intent)
  }

  private fun handleDeepLink(intent: Intent) {
    intent.resolveDeepLink()
      .onSuccess { backStack -> backStack?.let(appFlowViewModel::onDeepLink) }
      .onFailure { error -> Log.w("MainActivity", "Invalid deep link", error) }
  }
}
