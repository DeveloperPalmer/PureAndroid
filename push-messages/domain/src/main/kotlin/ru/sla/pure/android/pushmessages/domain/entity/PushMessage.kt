package ru.sla.pure.android.pushmessages.domain.entity

sealed interface PushMessage {
  val id: Long
  val title: String
  val description: String
  val channel: Channel

  enum class Channel { Default }

  data class OpenCharacter(
    val characterId: Int,
    override val id: Long,
    override val title: String,
    override val description: String,
    override val channel: Channel
  ) : PushMessage
}
