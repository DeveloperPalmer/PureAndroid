package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.sla.pure.android.feature.app.AppFlowHost
import ru.sla.pure.android.uikit.theme.AppTheme

/**
 * Единственная Activity: запускает корневой флоу приложения.
 */
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val appFlowComponent = (application as PureApplication).appFlowComponent
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
