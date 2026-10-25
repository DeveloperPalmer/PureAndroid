package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.sla.pure.android.navigation.AppNavigation
import ru.sla.pure.android.ui.theme.PureAndroidTheme

/**
 * `@AndroidEntryPoint` подключает Activity к графу Hilt: для неё создаётся `ActivityComponent`,
 * дочерний к `SingletonComponent`, а фабрикой ViewModel по умолчанию становится фабрика Hilt —
 * через неё `hiltViewModel()` создаёт `@HiltViewModel` с зависимостями
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PureAndroidTheme {
                AppNavigation()
            }
        }
    }
}
