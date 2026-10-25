package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Состояние списка городов. `@HiltViewModel` с `@Inject`-конструктором — и Hilt сам создаёт
 * ViewModel со всеми зависимостями, фабрику писать не нужно. Данных пока нет: заглушка
 */
@HiltViewModel
class CitiesViewModel @Inject constructor() : ViewModel() {

    val cityIds: List<Long> = listOf(1L, 2L, 3L)
}
