package ru.sla.pure.android.feature.map.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MapRoute : NavKey {

    @Serializable
    data object Map : MapRoute
}
