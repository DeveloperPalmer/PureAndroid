package ru.sla.pure.android.feature.cities.ui.routing

import ru.sla.pure.android.core.ui.Event

/**
 * События экранов фичи «Города» для её модуля routing. Описывают, что сделал пользователь,
 * а куда после этого перейти, решает routing: ui о навигации не знает
 */
sealed interface FlowEvent : Event {

    data class CityDetailsRequested(val cityId: Long) : FlowEvent

    data object CityDetailsDismissed : FlowEvent
}
