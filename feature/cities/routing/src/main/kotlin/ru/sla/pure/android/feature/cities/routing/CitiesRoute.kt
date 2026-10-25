package ru.sla.pure.android.feature.cities.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Список городов: корень вкладки «Города» */
@Serializable
data object CitiesRoute : NavKey

/** Детали города. Открываются поверх вкладок, поэтому ключ виден всем фичам */
@Serializable
data class CityDetailsRoute(val cityId: Long) : NavKey
