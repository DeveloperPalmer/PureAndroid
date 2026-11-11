package ru.sla.pure.android.feature.login.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface LoginRoute : NavKey {
  @Serializable
  data object Credentials : LoginRoute
}
