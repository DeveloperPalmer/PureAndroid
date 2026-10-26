package ru.sla.pure.android.feature.cities.ui.screen.cities

import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.cities.domain.CitiesModel
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import javax.inject.Inject

/**
 * Список городов на Orbit. Состояние — [ViewState], одноразовых эффектов у экрана нет (`Nothing`).
 * Города берёт из [CitiesModel] флоу, а клик по городу не обрабатывает сама, а сообщает о нём
 * роутингу событием. Создаётся из графа флоу, поэтому получает тот же [CitiesModel], что и
 * остальные экраны флоу
 */
class CitiesViewModel @Inject constructor(
    private val citiesModel: CitiesModel,
    private val eventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {

    // onCreate выполняется один раз, при первом обращении к контейнеру — обычно это подписка
    // экрана на состояние. Дальше состояние следует за моделью, пока жива ViewModel
    override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
        citiesModel.cityIds.collect { cityIds ->
            reduce { state.copy(cityIds = cityIds) }
        }
    }

    fun onCityClick(cityId: Long) = intent {
        eventSink.sendEvent(FlowEvent.CityDetailsRequested(cityId))
    }
}
