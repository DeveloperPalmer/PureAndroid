package ru.sla.pure.android.feature.cities.ui.routing

import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink

/** Подмена FlowEventSink для тестов: запоминает отправленные события, чтобы их можно было проверить */
class RecordingFlowEventSink : FlowEventSink {

    val events = mutableListOf<Event>()

    override fun sendEvent(event: Event) {
        events += event
    }
}
