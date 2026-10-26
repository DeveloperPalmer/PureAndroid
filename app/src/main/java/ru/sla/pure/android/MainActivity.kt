package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.FlowEventSource
import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.navigation.AppNavigation
import ru.sla.pure.android.uikit.theme.AppTheme
import javax.inject.Inject

/**
 * `@AndroidEntryPoint` подключает Activity к графу Hilt: для неё создаётся `ActivityComponent`,
 * дочерний к `SingletonComponent`, а фабрикой ViewModel по умолчанию становится фабрика Hilt —
 * через неё `hiltViewModel()` создаёт `@HiltViewModel` с зависимостями
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryProviderInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var flowNodes: Set<@JvmSuppressWildcards FlowNode>

    /** События экранов, которые ViewModel отправили через FlowEventSink */
    @Inject
    lateinit var flowEventSource: FlowEventSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation(
                    entryProviderInstallers = entryProviderInstallers,
                    flowNodes = flowNodes,
                    flowEventSource = flowEventSource,
                )
            }
        }
    }
}
