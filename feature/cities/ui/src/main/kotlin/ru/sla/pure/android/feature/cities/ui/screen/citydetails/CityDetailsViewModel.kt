package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent

/**
 * Состояние деталей города. `cityId` приходит из ключа навигации, а не из графа, поэтому
 * передаётся через assisted injection: зависимости из графа Hilt подставит сам, а `cityId`
 * передаёт экран через [Factory] при создании ViewModel
 */
@HiltViewModel(assistedFactory = CityDetailsViewModel.Factory::class)
class CityDetailsViewModel @AssistedInject constructor(
    @Assisted val cityId: Long,
    private val eventSink: FlowEventSink,
) : ViewModel() {

    fun onBack() {
        eventSink.sendEvent(FlowEvent.CityDetailsDismissed)
    }

    @AssistedFactory
    interface Factory {
        fun create(cityId: Long): CityDetailsViewModel
    }
}
