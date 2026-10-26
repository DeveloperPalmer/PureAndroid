package ru.sla.pure.android.feature.main.routing

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.main.routing.di.MainFlowComponent
import ru.sla.pure.android.feature.main.routing.di.MainFlowEntryPoint
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen
import javax.inject.Inject
import javax.inject.Provider

/**
 * Экраны фичи «Главная»: какой экран показать по ключу. Главный экран — корень флоу «Главная».
 * ViewModel экранов берутся из графа флоу, а живут в хранилище своей записи
 */
class MainEntryProviderInstaller @Inject constructor(
    private val component: Provider<MainFlowComponent.Builder>,
) : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install() {
        flow<MainFlowEntryPoint>(
            root = MainRoute.Main,
            component = { component.get().build() },
        ) {
            entry<MainRoute.Main>(metadata = showNavigationBar()) {
                MainScreen(viewModel = viewModel { mainViewModel() })
            }
        }
    }
}
