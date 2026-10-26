package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.runtime.Immutable

/**
 * Состояние главного экрана. Неизменяемый снимок: ViewModel не правит его, а заменяет новым
 * через `reduce { state.copy(…) }`. `@Immutable` сообщает Compose, что список внутри не меняется,
 * и экран не перерисовывается, пока не пришло новое состояние
 */
@Immutable
data class ViewState(
    val cityIds: List<Long> = emptyList(),
)
