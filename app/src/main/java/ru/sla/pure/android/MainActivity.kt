package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.sla.pure.android.feature.app.AppFlowHost
import ru.sla.pure.android.uikit.theme.AppTheme

/**
 * Единственная Activity: запускает корневой флоу приложения.
 */
class MainActivity : ComponentActivity() {
  private val appFlowViewModel by viewModels<AppFlowViewModel> {
    viewModelFactory {
      initializer {
        val app = application as PureApplication
        val foregroundComponent = app.foregroundFactory.create()
        AppFlowViewModel(foregroundComponent.appFlowFactory().create())
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val appFlowComponent = appFlowViewModel.component

    setContent {
      AppTheme {
        AppFlowHost(
          navigator = appFlowComponent.appNavigator(),
          entryProviderInstallers = appFlowComponent.entryProviderInstallers()
        )
      }
    }
  }
}
