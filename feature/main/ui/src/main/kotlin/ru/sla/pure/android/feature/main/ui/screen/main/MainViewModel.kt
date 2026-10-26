package ru.sla.pure.android.feature.main.ui.screen.main

import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import javax.inject.Inject

/**
 * Главный экран на Orbit. Состояние — [ViewState], одноразовых эффектов у экрана нет (`Nothing`).
 * Города берёт из [MainModel] флоу. Создаётся из графа флоу, поэтому получает тот же [MainModel],
 * что и остальные экраны флоу
 */
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
