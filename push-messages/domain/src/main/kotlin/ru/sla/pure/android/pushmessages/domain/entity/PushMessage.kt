package ru.sla.pure.android.pushmessages.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface PushMessage : Parcelable {
  val id: Long
  val title: String
  val description: String
  val channel: Channel

  enum class Channel { Default }

  @Parcelize
  data class OpenCharacter(
    val characterId: Int,
    override val id: Long,
    override val title: String,
    override val description: String,
    override val channel: Channel
  ) : PushMessage
}
