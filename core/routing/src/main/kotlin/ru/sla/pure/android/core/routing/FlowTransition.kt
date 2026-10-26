package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey

/** Ответ [FlowNode] на событие экрана: какой переход сделать. */
sealed interface FlowTransition {

    /**
     * Открыть экран [route].
     *
     * - Если это ключ вкладки, переключается вкладка.
     * - Иначе экран добавляется в стек текущей вкладки.
     */
    data class NavigateTo(val route: NavKey) : FlowTransition

    /** Шаг назад — то же, что системная кнопка «Назад». */
    data object Back : FlowTransition

    /** Событие принято, но переход не нужен. Следующие узлы его уже не получат. */
    data object Stay : FlowTransition

    /** Событие не для этого узла: его получит следующий [FlowNode]. */
    data object Ignore : FlowTransition
}
