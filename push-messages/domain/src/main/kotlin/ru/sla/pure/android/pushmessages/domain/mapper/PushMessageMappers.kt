package ru.sla.pure.android.pushmessages.domain.mapper

import ru.sla.pure.android.pushmessages.domain.PushMessageConstants
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage

fun extractPushMessageFromType(string: (String) -> String?): PushMessage? {
  return when (string(PushMessageConstants.TYPE)) {
    "character" -> {
      val characterId = requireNotNull(string(CHARACTER_ID)?.toIntOrNull()) { "Invalid characterId" }
      PushMessage.OpenCharacter(
        id = characterId.toLong(),
        characterId = characterId,
        title = string(PushMessageConstants.TITLE).orEmpty(),
        description = string(PushMessageConstants.BODY).orEmpty(),
        channel = PushMessage.Channel.Default,
      )
    }
    else -> {
      null
    }
  }
}

private const val CHARACTER_ID = "characterId"
