package ru.sla.pure.android.feature.main.data.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import ru.sla.pure.android.core.data.network.ServerJson
import ru.sla.pure.android.pushmessages.domain.PushMessageConstants
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage

fun JsonObject.toCharacterPushMessageDomainModel(): Result<PushMessage> {
  return try {
    val payload = ServerJson.decodeFromJsonElement<CharacterPushPayload>(this)
    require(payload.type == PushMessageConstants.TYPE_CHARACTER) { "Unsupported push type: ${payload.type}" }
    Result.success(
      PushMessage.OpenCharacter(
        id = payload.messageId,
        characterId = payload.characterId,
        title = payload.title,
        description = payload.body,
        channel = PushMessage.Channel.Default,
      )
    )
  } catch (error: IllegalArgumentException) {
    Result.failure(error)
  }
}

@Serializable
private data class CharacterPushPayload(
  @SerialName(PushMessageConstants.TYPE)
  val type: String = PushMessageConstants.TYPE_CHARACTER,
  @SerialName(PushMessageConstants.MESSAGE_ID)
  val messageId: Long,
  @SerialName(PushMessageConstants.CHARACTER_ID)
  val characterId: Int,
  @SerialName(PushMessageConstants.TITLE)
  val title: String,
  @SerialName(PushMessageConstants.BODY)
  val body: String,
)
