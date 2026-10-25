package ru.sla.pure.android.feature.cities.routing

import ru.sla.pure.android.core.routing.FlowNode
import ru.sla.pure.android.core.routing.FlowTransition
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import javax.inject.Inject

/**
 * Навигация фичи «Города»: по событию экрана решает, куда перейти. Обычный класс с `@Inject`:
 * если для решения понадобятся данные (например, репозиторий или фича-флаги), они приходят
 * через конструктор
 */
class CitiesFlowNode @Inject constructor() : FlowNode {

    override fun transition(event: Event): FlowTransition {
        return when (event) {
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
}
