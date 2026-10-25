package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey

/** Ответ [FlowNode] на событие экрана: какой переход сделать */
sealed interface FlowTransition {

    /** Открыть экран: ключ вкладки переключает вкладку, любой другой открывается в текущей */
    data class NavigateTo(val route: NavKey) : FlowTransition

    /** Шаг назад */
    data object Back : FlowTransition

    /** Событие своё, но переход не нужен */
    data object Stay : FlowTransition

    /** Событие не своё: его получает следующий [FlowNode] */
    data object Ignore : FlowTransition
}
