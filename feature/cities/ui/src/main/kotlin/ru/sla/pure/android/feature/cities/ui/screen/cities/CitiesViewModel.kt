package ru.sla.pure.android.feature.cities.ui.screen.cities

import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import javax.inject.Inject

/**
 * Список городов на Orbit. Состояние — [ViewState], одноразовых эффектов у экрана нет (`Nothing`).
 * Клик по городу ViewModel не обрабатывает сама, а сообщает о нём роутингу событием
 */
@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val eventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {

    // onCreate выполняется один раз, при первом обращении к контейнеру — обычно это подписка
    // экрана на состояние. Данных пока нет, поэтому вместо загрузки — заглушка
    override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
        reduce { state.copy(cityIds = STUB_CITY_IDS) }
    }

    fun onCityClick(cityId: Long) = intent {
        eventSink.sendEvent(FlowEvent.CityDetailsRequested(cityId))
    }
}

private val STUB_CITY_IDS = listOf(1L, 2L, 3L)
