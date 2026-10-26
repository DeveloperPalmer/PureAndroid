package ru.sla.pure.android.core.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Экран с уже привязанной ViewModel: его собирает UI-модуль в своём графе, а роутинг только
 * показывает. [P] — параметры экрана из ключа навигации, у экрана без параметров это `Unit`
 */
fun interface WiredComposableScreen<in P> {

    @Composable
    fun Content(params: P)

    companion object {

        /**
         * Связывает экран с ViewModel. Граф даёт только фабрику [create]: сама ViewModel создаётся
         * при первом показе и живёт в хранилище записи (`LocalViewModelStoreOwner`), то есть
         * переживает поворот и уходит вместе с записью из стека
         */
        inline fun <P, reified VM : ViewModel> bind(
            noinline create: (P) -> VM,
            noinline screen: @Composable (VM) -> Unit,
        ): WiredComposableScreen<P> = WiredComposableScreen { params ->
            screen(viewModel { create(params) })
        }

        /** [bind] для экрана без параметров */
        inline fun <reified VM : ViewModel> bind(
            noinline create: () -> VM,
            noinline screen: @Composable (VM) -> Unit,
        ): WiredComposableScreen<Unit> = bind<Unit, VM>({ create() }, screen)
    }
}
