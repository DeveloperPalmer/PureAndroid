package ru.sla.pure.android.core.routing

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

/**
 * DI-граф флоу: его собирает [flow] при первом показе экрана флоу. Граф флоу фичи наследует этот
 * интерфейс, а ViewModel экранов флоу вкладываются в его скоуп через
 * `@ContributesIntoMap(<скоуп флоу>, binding<ViewModel>()) @ViewModelKey` и создаются
 * в записях через `metroViewModel()`. Поэтому методов для ViewModel в графе флоу нет.
 *
 * Сколько живёт: пока корень флоу лежит в стеке. Когда корень уходит, граф отменяет
 * [flowCoroutineScope], и вместе с ним все корутины моделей флоу.
 */
interface FlowComponent : ViewModelGraph {

    /**
     * Scope моделей флоу. Граф флоу фичи отдаёт свой `@ForScope(<скоуп флоу>)` scope: без квалификатора
     * он совпал бы со scope родительского графа
     */
    fun flowCoroutineScope(): CoroutineScope

    @Binds
    val FlowViewModelFactory.bindMetroViewModelFactory: MetroViewModelFactory
}

/** Фабрика ViewModel графа флоу: создаёт ViewModel, вложенные в скоуп этого флоу */
class FlowViewModelFactory @Inject constructor(
    override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
    override val assistedFactoryProviders: Map<KClass<out ViewModel>, () -> ViewModelAssistedFactory>,
    override val manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>,
) : MetroViewModelFactory()
