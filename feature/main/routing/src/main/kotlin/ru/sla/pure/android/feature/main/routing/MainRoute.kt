package ru.sla.pure.android.feature.main.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : NavKey {
  @Serializable
  data object Main : MainRoute

  @Serializable
  data object Profile : MainRoute

  @Serializable
  data class CharacterDetails(val characterId: Int) : MainRoute
}
