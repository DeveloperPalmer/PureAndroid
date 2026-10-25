package ru.sla.pure.android.core.ui

/**
 * Событие экрана для роутинга: что произошло, а не куда идти. Каждая фича описывает свои события
 * в `ui/routing/FlowEvent.kt`, а решение о переходе принимает её модуль routing
 */
interface Event
