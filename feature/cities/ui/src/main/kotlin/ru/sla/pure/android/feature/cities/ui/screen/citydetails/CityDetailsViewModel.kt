package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent

/**
 * Детали города на Orbit. `cityId` приходит из ключа навигации, а не из графа, поэтому
 * передаётся через assisted injection: зависимости из графа Hilt подставит сам, а `cityId`
 * передаёт экран через [Factory] при создании ViewModel. Из него собирается начальное состояние
 */
@HiltViewModel(assistedFactory = CityDetailsViewModel.Factory::class)
class CityDetailsViewModel @AssistedInject constructor(
    @Assisted
    private val cityId: Long,
    private val eventSink: FlowEventSink,
) : BaseViewModel<ViewState, Nothing>() {

    @AssistedFactory
    interface Factory {
        fun create(cityId: Long): CityDetailsViewModel
    }

    override val container = orbitContainer<ViewState, Nothing>(
        ViewState(
            cityId = cityId
        )
    )

    fun onBack() = intent {
        eventSink.sendEvent(FlowEvent.CityDetailsDismissed)
    }
}
