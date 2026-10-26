package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.compose.runtime.Immutable

/**
 * Состояние экрана списка городов. Неизменяемый снимок: ViewModel не правит его, а заменяет новым
 * через `reduce { state.copy(…) }`. `@Immutable` сообщает Compose, что список внутри не меняется,
 * и экран не перерисовывается, пока не пришло новое состояние
 */
@Immutable
data class ViewState(
    val cityIds: List<Long> = emptyList(),
)
