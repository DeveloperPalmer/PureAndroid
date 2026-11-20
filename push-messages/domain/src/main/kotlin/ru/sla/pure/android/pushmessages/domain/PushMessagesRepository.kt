package ru.sla.pure.android.pushmessages.domain

import kotlinx.coroutines.flow.Flow
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage

interface PushMessagesRepository {
  val messages: Flow<Result<PushMessage>>
}
