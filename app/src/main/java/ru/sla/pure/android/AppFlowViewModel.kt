package ru.sla.pure.android

import androidx.lifecycle.ViewModel
import ru.sla.pure.android.di.ForegroundComponent

/** Сохраняет навигационный граф при пересоздании Activity. */
internal class AppFlowViewModel(val foregroundComponent: ForegroundComponent) : ViewModel()
