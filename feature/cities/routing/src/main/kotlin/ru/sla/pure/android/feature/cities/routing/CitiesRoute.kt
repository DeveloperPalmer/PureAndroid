package ru.sla.pure.android.feature.cities.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CitiesRoute : NavKey {

    @Serializable
    data object Cities : CitiesRoute

    @Serializable
    data class CityDetails(val cityId: Long) : CitiesRoute
}
