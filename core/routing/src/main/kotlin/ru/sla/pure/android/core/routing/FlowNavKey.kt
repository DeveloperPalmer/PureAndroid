package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Стабильный ключ контейнера флоу в родительском стеке. */
@Serializable
data class FlowNavKey(val id: String) : NavKey
