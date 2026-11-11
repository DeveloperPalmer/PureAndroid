package ru.sla.pure.android.feature.app.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
  @Serializable
  data object InitialResolve : AppRoute
}
