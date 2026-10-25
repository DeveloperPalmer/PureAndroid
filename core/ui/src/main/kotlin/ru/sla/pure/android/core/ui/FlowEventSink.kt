package ru.sla.pure.android.core.ui

/**
 * Куда ViewModel отправляет [Event]. Реализация живёт в `:core:routing` и попадает к ViewModel
 * через Hilt, поэтому модулю ui не нужно знать ни роутинг, ни навигацию
 */
interface FlowEventSink {

    fun sendEvent(event: Event)
}
