package ru.sla.pure.android.core.routing

import kotlinx.coroutines.CoroutineScope

/**
 * DI-граф флоу: его собирает [flow] при первом показе экрана флоу, а записи флоу берут из него
 * ViewModel экранов. Граф флоу фичи наследует этот интерфейс и добавляет методы своих ViewModel.
 *
 * Сколько живёт: пока корень флоу лежит в стеке. Когда корень уходит, граф отменяет
 * [flowCoroutineScope], и вместе с ним все корутины моделей флоу.
 */
interface FlowComponent {

    /**
     * Scope моделей флоу. Граф флоу фичи отдаёт свой `@ForScope(<скоуп флоу>)` scope: без квалификатора
     * он совпал бы со scope родительского графа
     */
    fun flowCoroutineScope(): CoroutineScope
}
