package ru.sla.pure.android.pushmessages.android

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import ru.sla.pure.android.pushmessages.data.RawMessage
import ru.sla.pure.android.pushmessages.data.di.PushMessagingSinkHolder

// Lint 32.4.1 проверяет только onNewToken(). При регистрации по FID SDK вызывает onRegistered().
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebasePushMessagingService : FirebaseMessagingService() {
  private val sink by lazy { (application as PushMessagingSinkHolder).pushMessagingSink() }

  override fun onMessageReceived(message: RemoteMessage) {
    val rawMessage = RawMessage(message.data, message.messageId)
    // FCM вызывает этот callback на рабочем потоке и завершает доставку после возврата.
    runBlocking { sink.onNewRawMessage(rawMessage) }
  }

  override fun onRegistered(installationId: String) {
    sink.onRegistered(installationId)
  }
}
