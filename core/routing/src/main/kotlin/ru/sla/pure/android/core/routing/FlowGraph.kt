package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Граф флоу для записей навигации. Как его собрать ([create]), знает наследник в DI-слое флоу,
 * а записи только берут из него [S] через [current]
 */
open class FlowGraph<S : Any>(private val create: () -> S) {

    /**
     * [S] текущего флоу. Создаётся один раз на флоу и хранится в хранилище флоу
     * ([LocalFlowViewModelStoreOwner]): все записи флоу получают один и тот же экземпляр, он
     * переживает поворот и уходит вместе с корнем флоу из стека
     */
    @Composable
    fun current(): S {
        val holder = viewModel(
            modelClass = Holder::class,
            viewModelStoreOwner = LocalFlowViewModelStoreOwner.current,
            key = this::class.qualifiedName,
            factory = viewModelFactory { initializer { Holder(create()) } },
        )
        @Suppress("UNCHECKED_CAST")
        return holder.graph as S
    }

    /** Держит граф флоу в хранилище флоу и задаёт ему время жизни */
    private class Holder(val graph: Any) : ViewModel()
}
