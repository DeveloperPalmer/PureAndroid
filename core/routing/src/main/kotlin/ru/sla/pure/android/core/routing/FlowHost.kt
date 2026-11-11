package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/** Отображение контейнера предоставляет приложение, фичи объявляют его содержимое через flow. */
interface FlowHost {
  @Composable
  fun <C : FlowComponent> Content(
    tabs: List<NavigationTab>,
    component: (Navigator) -> C,
    entries: EntryProviderScope<NavKey>.(C) -> Unit,
  )
}

val LocalFlowHost = staticCompositionLocalOf<FlowHost> {
  error("FlowHost is not provided")
}
