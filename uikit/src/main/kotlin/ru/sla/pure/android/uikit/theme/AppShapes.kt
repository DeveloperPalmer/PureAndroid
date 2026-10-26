package ru.sla.pure.android.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/** Скругления. Здесь только те, что повторяются; разовую форму компонент задаёт сам */
@Immutable
class AppShapes {
    val round8 = RoundedCornerShape(size = 8.dp)
    val round12 = RoundedCornerShape(size = 12.dp)
    val round16 = RoundedCornerShape(size = 16.dp)
    val round24 = RoundedCornerShape(size = 24.dp)
}

internal val LocalAppShapes = staticCompositionLocalOf<AppShapes> {
    error("No AppShapes provided: оберните экран в AppTheme")
}
