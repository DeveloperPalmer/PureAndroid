package ru.sla.pure.android.feature.cities.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sla.pure.android.feature.cities.domain.di.CitiesScope
import javax.inject.Inject

/**
 * Состояние флоу «Города». Один экземпляр на флоу: экраны флоу читают одни и те же данные.
 */
@CitiesScope
class CitiesModel @Inject constructor() {

    private val _cityIds = MutableStateFlow(STUB_CITY_IDS)
    val cityIds: StateFlow<List<Long>> = _cityIds.asStateFlow()
}

private val STUB_CITY_IDS = listOf(1L, 2L, 3L)
