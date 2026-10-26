package ru.sla.pure.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.ViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.get
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import ru.sla.pure.android.core.routing.FlowKey
import ru.sla.pure.android.core.routing.LocalFlowViewModelStoreOwner

/**
 * Даёт записи хранилище ViewModel её флоу через [LocalFlowViewModelStoreOwner]. Запись с метаданными
 * флоу берёт хранилище корня по его contentKey, запись без них — своё собственное.
 *
 * Работает в паре с `ViewModelStoreNavEntryDecorator` на том же [viewModelStoreProvider]: тот создаёт
 * хранилища записей и очищает их, когда запись уходит из стека, а этот только находит нужное. Пока
 * запись флоу на экране, она держит хранилище корня, и оно не очистится раньше неё. Корень флоу должен
 * лежать в стеке под его записями: иначе хранилище флоу некому очистить.
 */
class FlowViewModelStoreNavEntryDecorator<T : Any>(
    viewModelStoreProvider: ViewModelStoreProvider,
) : NavEntryDecorator<T>(
    decorate = { entry ->
        val flowKey = entry.metadata[FlowKey] ?: entry.contentKey
        val owner = rememberViewModelStoreOwner(
            key = flowKey,
            provider = viewModelStoreProvider,
            savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
        )
        CompositionLocalProvider(LocalFlowViewModelStoreOwner provides owner) {
            entry.Content()
        }
    },
)

@Composable
fun <T : Any> rememberFlowViewModelStoreNavEntryDecorator(
    viewModelStoreProvider: ViewModelStoreProvider,
): FlowViewModelStoreNavEntryDecorator<T> = remember(viewModelStoreProvider) {
    FlowViewModelStoreNavEntryDecorator(viewModelStoreProvider)
}
