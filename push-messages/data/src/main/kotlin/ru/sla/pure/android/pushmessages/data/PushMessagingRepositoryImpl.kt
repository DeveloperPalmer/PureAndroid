package ru.sla.pure.android.pushmessages.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import ru.sla.pure.android.feature.main.data.push.toCharacterPushMessageDomainModel
import ru.sla.pure.android.pushmessages.domain.PushMessageConstants
import ru.sla.pure.android.pushmessages.domain.PushMessagesRepository
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage
import java.util.concurrent.atomic.AtomicLong

@ContributesBinding(AppScope::class)
class PushMessagingRepositoryImpl @Inject constructor(
  messagingService: PushMessagingService,
) : PushMessagesRepository {
  private val nextId = AtomicLong(1000)

  override val messages: Flow<Result<PushMessage>> = messagingService.messages.map { message ->
    val data = buildJsonObject {
      put(PushMessageConstants.MESSAGE_ID, nextId.getAndIncrement())
      message.data.forEach { (key, value) -> put(key, value) }
    }
    data.toCharacterPushMessageDomainModel()
  }
}
