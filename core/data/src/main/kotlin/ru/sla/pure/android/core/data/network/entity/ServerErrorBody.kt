package ru.sla.pure.android.core.data.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ServerErrorBody(
  @SerialName("code")
  val code: String? = null,
  @SerialName("error")
  val message: String? = null,
)
