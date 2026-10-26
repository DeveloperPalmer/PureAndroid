package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.cancel

/**
 * Даёт записям флоу DI-компонент флоу [C].
 *
 * - [key] — по нему компонент находится в хранилище флоу.
 * - [create] — как собрать компонент. Это знает фича, а `FlowGraph` только вызывает.
 * - [current] — как записи получают компонент. Все записи флоу получают один и тот же экземпляр.
 *
 * Сам `FlowGraph` ничего не хранит: компонент лежит в хранилище флоу, а не в этом объекте. Поэтому
 * `FlowGraph` можно спокойно создавать заново при каждой пересборке записей, компонент при этом
 * не потеряется.
 */
@PublishedApi
internal class FlowGraph<C : FlowComponent>(
    private val key: String,
    private val create: () -> C,
) {

    /**
     * Возвращает компонент [C] флоу, в котором показана текущая запись.
     *
     * При первом вызове во флоу компонент собирается через [create] и кладётся в хранилище флоу
     * ([LocalFlowViewModelStoreOwner]). Дальше все записи флоу получают этот же экземпляр.
     *
     * Сколько живёт компонент:
     * - переживает поворот экрана;
     * - удаляется, когда корень флоу уходит из стека, и тогда же отменяется его scope.
     */
    @Composable
    fun current(): C {
        val holder = viewModel(
            key = key,
            modelClass = Holder::class,
            viewModelStoreOwner = LocalFlowViewModelStoreOwner.current,
            factory = viewModelFactory { initializer { Holder(create()) } },
        )
        @Suppress("UNCHECKED_CAST")
        return holder.component as C
    }

    /**
     * Обёртка-ViewModel над компонентом: через неё компонент лежит в хранилище флоу и удаляется
     * вместе с ним. Хранилище очищается, когда корень флоу уходит из стека или Activity завершается,
     * и тогда обёртка отменяет scope флоу: корутины моделей флоу не переживают сам флоу.
     */
    private class Holder(val component: FlowComponent) : ViewModel() {
        override fun onCleared() {
            component.flowCoroutineScope().cancel()
        }
    }
}
