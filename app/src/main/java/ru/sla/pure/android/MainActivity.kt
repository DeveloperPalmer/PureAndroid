package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.sla.pure.android.navigation.AppNavigation
import ru.sla.pure.android.uikit.theme.AppTheme

/**
 * Единственная Activity: показывает навигацию приложения. Экраны, переходы и события экранов
 * берёт из графа приложения
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appGraph = (application as PureApplication).appGraph
        setContent {
            AppTheme {
                AppNavigation(
                    entryProviderInstallers = appGraph.entryProviderInstallers(),
                    flowNodes = appGraph.flowNodes(),
                    flowEventSource = appGraph.flowEventSource(),
                )
            }
        }
    }
}
