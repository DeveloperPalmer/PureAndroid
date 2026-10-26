package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope

/**
 * Главный экран на Orbit. Состояние — [ViewState], одноразовых эффектов у экрана нет (`Nothing`).
 * Города берёт из [MainModel] флоу. Вкладывается в граф флоу «Главная» (`MainScope`), поэтому
 * получает тот же [MainModel], что и остальные экраны флоу. `binding<ViewModel>()`: в набор
 * ViewModel графа она попадает как `ViewModel`, а не как свой прямой родитель `BaseViewModel`
 */
@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class MainViewModel @Inject constructor(
    private val mainModel: MainModel,
) : BaseViewModel<ViewState, Nothing>() {

    // onCreate выполняется один раз, при первом обращении к контейнеру — обычно это подписка
    // экрана на состояние. Дальше состояние следует за моделью, пока жива ViewModel
    override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
        mainModel.cityIds.collect { cityIds ->
            reduce { state.copy(cityIds = cityIds) }
        }
    }
}
