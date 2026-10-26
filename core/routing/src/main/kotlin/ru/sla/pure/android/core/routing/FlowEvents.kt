package ru.sla.pure.android.core.routing

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink

interface FlowEventSource {
    val events: Flow<Event>
}

@SingleIn(AppScope::class)
class FlowEventMediator @Inject constructor() : FlowEventSink, FlowEventSource {

    private val channel = Channel<Event>(Channel.BUFFERED)

    override val events: Flow<Event> = channel.receiveAsFlow()

    override fun sendEvent(event: Event) {
        channel.trySend(event)
    }
}
