package ru.sla.pure.android

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import ru.sla.pure.android.di.ForegroundComponent

/** Сохраняет навигационный граф при пересоздании Activity. */
internal class AppFlowViewModel(val foregroundComponent: ForegroundComponent) : ViewModel() {
  fun onDeepLink(backStack: List<NavKey>) {
    foregroundComponent.navigationHostState().applyDeepLink(backStack)
  }
}
