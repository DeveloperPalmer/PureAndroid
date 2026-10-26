package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent

/**
 * Детали города на Orbit. `cityId` приходит из ключа навигации, а не из графа, поэтому
 * передаётся через assisted injection: зависимости подставит граф флоу, а `cityId` передаёт
 * запись роутинга через [Factory] при создании ViewModel. Из него собирается начальное состояние
 */
class CityDetailsViewModel @AssistedInject constructor(
    @Assisted
    private val cityId: Long,
    private val eventSink: FlowEventSink,
) : BaseViewModel<ViewState, SideEffect>() {

    @AssistedFactory
    interface Factory {
        fun create(cityId: Long): CityDetailsViewModel
    }

    override val container = orbitContainer<ViewState, SideEffect>(
        ViewState(
            cityId = cityId
        )
    )

    fun onShowMessage() = intent {
        postSideEffect(SideEffect.ShowCityMessage(state.cityId))
    }

    fun onBack() = intent {
        eventSink.sendEvent(FlowEvent.CityDetailsDismissed)
    }
}
