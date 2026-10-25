package ru.sla.pure.android.core.routing

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.core.ui.FlowEventSink
import javax.inject.Inject

/** Откуда приложение читает события экранов, чтобы передать их переходам */
interface FlowEventSource {

    val events: Flow<Event>
}

@ActivityRetainedScoped
class FlowEventMediator @Inject constructor() : FlowEventSink, FlowEventSource {

    private val channel = Channel<Event>(Channel.BUFFERED)

    override val events: Flow<Event> = channel.receiveAsFlow()

    override fun sendEvent(event: Event) {
        channel.trySend(event)
    }
}
