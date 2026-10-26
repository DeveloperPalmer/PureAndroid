package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Граф флоу для записей навигации: точка входа [E] в компонент флоу. Как его собрать ([create]),
 * знает фича, а записи только берут его через [current]. Сам объект ничего не хранит, поэтому его
 * можно создавать заново при каждой сборке записей: граф находится в хранилище флоу по [key]
 */
@PublishedApi
internal class FlowGraph<E : Any>(
    private val key: String,
    private val create: () -> E,
) {

    /**
     * [E] текущего флоу. Создаётся один раз на флоу и хранится в хранилище флоу
     * ([LocalFlowViewModelStoreOwner]): все записи флоу получают один и тот же экземпляр, он
     * переживает поворот и уходит вместе с корнем флоу из стека
     */
    @Composable
    fun current(): E {
        val holder = viewModel(
            modelClass = Holder::class,
            viewModelStoreOwner = LocalFlowViewModelStoreOwner.current,
            key = key,
            factory = viewModelFactory { initializer { Holder(create()) } },
        )
        @Suppress("UNCHECKED_CAST")
        return holder.graph as E
    }

    /** Держит граф флоу в хранилище флоу и задаёт ему время жизни */
    private class Holder(val graph: Any) : ViewModel()
}
