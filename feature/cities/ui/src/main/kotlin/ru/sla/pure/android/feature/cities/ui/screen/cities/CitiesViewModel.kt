package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import javax.inject.Inject

/**
 * Состояние списка городов. `@HiltViewModel` с `@Inject`-конструктором — и Hilt сам создаёт
 * ViewModel со всеми зависимостями, фабрику писать не нужно. Данных пока нет: заглушка.
 * Клик по городу ViewModel не обрабатывает сама, а сообщает о нём роутингу событием
 */
@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val eventSink: FlowEventSink,
) : ViewModel() {

    val cityIds: List<Long> = listOf(1L, 2L, 3L)

    fun onCityClick(cityId: Long) {
        eventSink.sendEvent(FlowEvent.CityDetailsRequested(cityId))
    }
}
