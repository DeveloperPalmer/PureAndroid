package ru.sla.pure.android.feature.main.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : NavKey {

    @Serializable
    data object Main : MainRoute
}
