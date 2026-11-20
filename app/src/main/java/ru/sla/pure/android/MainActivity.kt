package ru.sla.pure.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.sla.pure.android.app.routing.AppFlowHost
import ru.sla.pure.android.core.routing.deeplink.PendingDeepLink
import ru.sla.pure.android.feature.app.routing.AppRoute
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

    splashScreen.setKeepOnScreenCondition {
      val route = hostState.currentRoute
      route == null || route == AppRoute.InitialResolve
    }

    setContent {
      AppTheme {
        AppFlowHost(
          hostState = hostState,
          entryProviderInstallers = foregroundComponent.entryProviderInstallers()
        )
      }
    }
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
