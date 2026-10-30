package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.compose.runtime.Immutable
import ru.sla.pure.android.feature.main.domain.entity.Character

/**
 * Состояние главного экрана. Неизменяемый снимок: ViewModel не правит его, а заменяет новым
 * через `reduce { state.copy(…) }`. `@Immutable` сообщает Compose, что список внутри не меняется,
 * и экран не перерисовывается, пока не пришло новое состояние
 */
@Immutable
data class ViewState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = true,
    val nextPage: Int? = null,
    val failedPage: Int? = null,
    val error: LoadError? = null,
)

enum class LoadError { Connection, Server, Unexpected }
