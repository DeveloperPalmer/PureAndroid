package ru.sla.pure.android.core.routing

import ru.sla.pure.android.core.ui.Event

/**
 * Навигация одной фичи: по событию экрана решает, какой переход сделать.
 *
 * Сам узел никуда не переходит, а только возвращает [FlowTransition]. Выполняет переход [dispatch].
 *
 * Модуль routing фичи кладёт свой узел в общий `Set<FlowNode>` через `@Binds @IntoSet`.
 */
fun interface FlowNode {

    fun transition(event: Event): FlowTransition
}

/**
 * Передаёт событие узлам [flowNodes] по очереди и выполняет переход первого узла, который его
 * принял.
 *
 * Узел принял событие, если вернул что угодно, кроме [FlowTransition.Ignore]. Если событие
 * не принял ни один узел, ничего не происходит.
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
