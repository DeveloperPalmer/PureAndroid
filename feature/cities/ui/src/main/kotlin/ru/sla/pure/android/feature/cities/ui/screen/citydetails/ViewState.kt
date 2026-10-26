package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import androidx.compose.runtime.Immutable

/** Состояние экрана деталей города. Пока в нём только id города из ключа навигации */
@Immutable
data class ViewState(
    val cityId: Long,
)
