package ru.sla.pure.android.core.routing

import ru.sla.pure.android.core.ui.Event

/**
 * Роутинг фичи: по событию экрана решает, какой переход сделать, но сам не навигирует — только
 * возвращает [FlowTransition]. Модули routing кладут свои узлы в общий `Set` через Hilt
 * (`@Provides @IntoSet`)
 */
fun interface FlowNode {

    fun transition(event: Event): FlowTransition
}

/**
 * Передаёт событие узлам по очереди, пока один из них его не примет, и выполняет его переход.
 * Если все узлы ответили [FlowTransition.Ignore], событие ни к чему не приводит
 */
fun Navigator.dispatch(event: Event, flowNodes: Set<FlowNode>) {
    for (flowNode in flowNodes) {
        when (val transition = flowNode.transition(event)) {
            is FlowTransition.NavigateTo -> navigateTo(transition.route)
            is FlowTransition.Back -> navigateBack()
            is FlowTransition.Stay -> Unit
            is FlowTransition.Ignore -> continue
        }
        return
    }
}
