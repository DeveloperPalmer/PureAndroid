package ru.sla.pure.android.pushmessages.android

import android.os.Bundle
import android.util.Log
import androidx.core.os.BundleCompat
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage
import ru.sla.pure.android.pushmessages.domain.mapper.extractPushMessageFromType

private const val EXTRA_PUSH_ACTION = "ru.sla.pure.android.extra.PUSH_ACTION"

internal fun PushMessage.buildIntentExtras(): Result<Bundle> = runCatching {
  Bundle().apply { putParcelable(EXTRA_PUSH_ACTION, this@buildIntentExtras) }
}

fun Bundle.extractPushMessage(): PushMessage? {
  classLoader = PushMessage::class.java.classLoader

  if (!containsKey(EXTRA_PUSH_ACTION)) {
    return extractPushMessageFromType(::getString)
  }
  val pushMessage = BundleCompat.getParcelable(
    this,
    EXTRA_PUSH_ACTION,
    PushMessage::class.java
  )
  Log.d("DEEPLINK", "start configure deeplink from pushMessage: ${pushMessage ?: "no message"}")
  return requireNotNull(pushMessage) { "Invalid push message extra" }
}
