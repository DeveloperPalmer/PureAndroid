package ru.sla.pure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.navigation.AppNavigation
import ru.sla.pure.android.ui.theme.PureAndroidTheme
import javax.inject.Inject

/**
 * `@AndroidEntryPoint` подключает Activity к графу Hilt: для неё создаётся `ActivityComponent`,
 * дочерний к `SingletonComponent`, а фабрикой ViewModel по умолчанию становится фабрика Hilt —
 * через неё `hiltViewModel()` создаёт `@HiltViewModel` с зависимостями
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Записи всех фич: каждый модуль routing кладёт свои через `@IntoSet`.
     * `@JvmSuppressWildcards` нужен Dagger: без него Kotlin отдаёт Java тип `Set<? extends …>`,
     * а такого ключа в графе нет
     */
    @Inject
    lateinit var entryProviderInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PureAndroidTheme {
                AppNavigation(entryProviderInstallers = entryProviderInstallers)
            }
        }
    }
}
