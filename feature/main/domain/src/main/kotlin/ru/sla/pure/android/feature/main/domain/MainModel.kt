package ru.sla.pure.android.feature.main.domain

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sla.pure.android.feature.main.domain.di.MainScope

@SingleIn(MainScope::class)
@Inject
class MainModel {

    private val _cityIds = MutableStateFlow(STUB_CITY_IDS)
    val cityIds: StateFlow<List<Long>> = _cityIds.asStateFlow()
}

private val STUB_CITY_IDS = listOf(1L, 2L, 3L)
