package ru.sla.pure.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.sla.pure.android.app.routing.AppFlowHost
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
        val foregroundComponent = app.foregroundFactory.create(this@MainActivity)
        AppFlowViewModel(foregroundComponent)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    window.setNavigationBarColorCompat(Color.TRANSPARENT, contrastEnforced = false)

    val foregroundComponent = appFlowViewModel.foregroundComponent
    val appNavigator = foregroundComponent.appNavigator()

    splashScreen.setKeepOnScreenCondition {
      when (appNavigator.currentRoute) {
        null, AppRoute.InitialResolve -> true
        else -> false
      }
    }

    setContent {
      AppTheme {
        AppFlowHost(
          flowRoute = AppRoute.InitialResolve,
          navigator = appNavigator,
          entryProviderInstallers = foregroundComponent.entryProviderInstallers()
        )
      }
    }
  }
}
