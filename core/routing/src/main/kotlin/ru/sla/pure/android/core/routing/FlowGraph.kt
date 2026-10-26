package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Даёт записям флоу точку входа [E] в DI-граф флоу.
 *
 * - [key] — по нему граф находится в хранилище флоу.
 * - [create] — как собрать граф. Это знает фича, а `FlowGraph` только вызывает.
 * - [current] — как записи получают граф. Все записи флоу получают один и тот же экземпляр.
 *
 * Сам `FlowGraph` ничего не хранит: граф лежит в хранилище флоу, а не в этом объекте. Поэтому
 * `FlowGraph` можно спокойно создавать заново при каждой пересборке записей, граф при этом
 * не потеряется.
 */
@PublishedApi
internal class FlowGraph<E : Any>(
    private val key: String,
    private val create: () -> E,
) {

    /**
     * Возвращает точку входа [E] флоу, в котором показана текущая запись.
     *
     * При первом вызове во флоу граф собирается через [create] и кладётся в хранилище флоу
     * ([LocalFlowViewModelStoreOwner]). Дальше все записи флоу получают этот же экземпляр.
     *
     * Сколько живёт граф:
     * - переживает поворот экрана;
     * - удаляется, когда корень флоу уходит из стека.
     */
    @Composable
    fun current(): E {
        val holder = viewModel(
            key = key,
            modelClass = Holder::class,
            viewModelStoreOwner = LocalFlowViewModelStoreOwner.current,
            factory = viewModelFactory { initializer { Holder(create()) } },
        )
        @Suppress("UNCHECKED_CAST")
        return holder.graph as E
    }

    /**
     * Обёртка-ViewModel над графом: через неё граф лежит в хранилище флоу и удаляется вместе с ним.
     */
    private class Holder(val graph: Any) : ViewModel()
}
