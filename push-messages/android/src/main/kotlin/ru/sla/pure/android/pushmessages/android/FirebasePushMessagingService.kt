package ru.sla.pure.android.pushmessages.android

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.sla.pure.android.pushmessages.data.PushMessagingSink
import ru.sla.pure.android.pushmessages.data.RawMessage
import ru.sla.pure.android.pushmessages.data.di.PushMessagingSinkHolder

// Lint 32.4.1 проверяет только onNewToken(). При регистрации по FID SDK вызывает onRegistered().
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebasePushMessagingService : FirebaseMessagingService() {
  private var sink: PushMessagingSink? = null

  override fun onCreate() {
    super.onCreate()
    sink = (application as PushMessagingSinkHolder).pushMessagingSink()
  }

  override fun onMessageReceived(message: RemoteMessage) {
    sink?.onNewRawMessage(RawMessage(message.data, message.messageId))
  }

  override fun onRegistered(installationId: String) {
    sink?.onRegistered(installationId)
  }
}
