package ru.sla.pure.android

import androidx.lifecycle.ViewModel
import ru.sla.pure.android.feature.app.di.AppFlowComponent

/** Сохраняет навигационный граф при пересоздании Activity. */
internal class AppFlowViewModel(val component: AppFlowComponent) : ViewModel()
