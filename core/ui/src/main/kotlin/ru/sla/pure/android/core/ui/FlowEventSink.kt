package ru.sla.pure.android.core.ui

/** Событие флоу, по которому routing выбирает переход. */
interface Event

fun interface FlowEventSink {
  fun sendEvent(event: Event)
}
