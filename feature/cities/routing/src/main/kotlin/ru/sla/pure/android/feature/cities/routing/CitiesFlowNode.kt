package ru.sla.pure.android.feature.cities.routing

import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.core.routing.FlowTransition
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import javax.inject.Inject

class CitiesFlowNode @Inject constructor() : FlowNode {

    override fun transition(event: Event): FlowTransition = when (event) {
        is FlowEvent.CityDetailsRequested -> {
            FlowTransition.NavigateTo(CitiesRoute.CityDetails(event.cityId))
        }
        is FlowEvent.CityDetailsDismissed -> {
            FlowTransition.Back
        }
        else -> {
            FlowTransition.Ignore
        }
    }
}
