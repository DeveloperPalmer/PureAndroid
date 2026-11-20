package ru.sla.pure.android.core.routing.deeplink

import androidx.lifecycle.SavedStateHandle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlinx.serialization.builtins.ListSerializer

class PendingDeepLink(
  private val savedStateHandle: SavedStateHandle,
  restoreLegacyDestination: (NavKey) -> List<NavKey>,
) {
  private val serializer = ListSerializer(NavKeySerializer<NavKey>())

  init {
    val legacy = savedStateHandle.get<SavedState>(LEGACY_KEY)
    if (legacy != null && KEY !in savedStateHandle) {
      val destination = decodeFromSavedState(NavKeySerializer<NavKey>(), legacy)
      accept(restoreLegacyDestination(destination))
    }
    savedStateHandle.remove<SavedState>(LEGACY_KEY)
  }

  val backStack: List<NavKey>?
    get() = savedStateHandle.get<SavedState>(KEY)?.let { decodeFromSavedState(serializer, it) }

  fun accept(backStack: List<NavKey>) {
    savedStateHandle[KEY] = encodeToSavedState(serializer, backStack)
  }

  fun clear() {
    savedStateHandle.set<SavedState?>(KEY, null)
  }

  private companion object {
    const val KEY = "pending_deep_link_back_stack"
    const val LEGACY_KEY = "pending_deep_link_destination"
  }
}
