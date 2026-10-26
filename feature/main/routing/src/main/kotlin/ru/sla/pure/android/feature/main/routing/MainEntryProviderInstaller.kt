package ru.sla.pure.android.feature.main.routing

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.metroViewModel
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.flow
import ru.sla.pure.android.core.routing.showNavigationBar
import ru.sla.pure.android.feature.main.routing.di.MainFlowComponent
import ru.sla.pure.android.feature.main.ui.screen.main.MainScreen

/**
 * Экраны фичи «Главная»: какой экран показать по ключу. Главный экран — корень флоу «Главная».
 * ViewModel экранов берутся из графа флоу, а живут в хранилище своей записи.
 * `@ContributesIntoSet` кладёт установщик в общий `Set<EntryProviderInstaller>` приложения.
 * Главный экран пока никуда не ведёт, поэтому навигатор не используется
 */
@ContributesIntoSet(AppScope::class)
class MainEntryProviderInstaller @Inject constructor(
    private val componentFactory: MainFlowComponent.Factory,
) : EntryProviderInstaller {

    override fun EntryProviderScope<NavKey>.install(navigator: Navigator) {
        flow(
            root = MainRoute.Main,
            component = { componentFactory.create() },
        ) {
            entry<MainRoute.Main>(metadata = showNavigationBar()) {
                MainScreen(viewModel = metroViewModel())
            }
        }
    }
}
