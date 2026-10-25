package ru.sla.pure.android.core.ui.mvi

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.OrbitContainerHost

/**
 * База ViewModel экрана на Orbit (MVI). Экран показывает неизменяемое состояние [S] и вызывает методы
 * ViewModel. Каждый метод запускает `intent { }`, а внутри него ViewModel:
 * - меняет состояние через `reduce { state.copy(…) }`;
 * - шлёт экрану одноразовый эффект [SE] через `postSideEffect` — снэкбар, скролл.
 *
 * События навигации ViewModel шлёт сама, через внедрённый `FlowEventSink`: так по конструктору
 * видно, какие ViewModel говорят с роутингом.
 *
 * Контейнер наследник создаёт через `orbitContainer(initialState)` из orbit-viewmodel: он живёт
 * в `viewModelScope` и очищается вместе с ViewModel
 */
abstract class BaseViewModel<S : Any, SE : Any> : ViewModel(), OrbitContainerHost<S, S, SE>
