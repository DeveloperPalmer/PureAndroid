package ru.sla.pure.android

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import ru.sla.pure.android.di.AppComponent
import ru.sla.pure.android.di.ForegroundComponent
import ru.sla.pure.android.pushmessages.data.PushMessagingSink
import ru.sla.pure.android.pushmessages.data.di.PushMessagingSinkHolder

/**
 * Точка входа DI: держит [appComponent] — корневой граф Metro. Он создаётся при первом обращении
 * и живёт до конца процесса
 */
open class PureApplication : Application(), PushMessagingSinkHolder {
  val appComponent: AppComponent by lazy { createGraphFactory<AppComponent.Factory>().create(this) }
  open val foregroundFactory: ForegroundComponent.Factory
    get() = appComponent.foregroundFactory()

  override fun onCreate() {
    super.onCreate()
    appComponent.applicationScope().launch(start = CoroutineStart.UNDISPATCHED) {
      appComponent.pushMessagesRepository().messages.collect { result ->
        result
          .onSuccess { message ->
            if (BuildConfig.DEBUG) {
              Log.d("PureFCM", "Mapped push message: id=${message.id}")
            }
          }
          .onFailure { error -> Log.w("PureFCM", "Invalid push message", error) }
      }
    }
    FirebaseMessaging.getInstance()
      .register()
      .addOnFailureListener { error -> Log.e("PureFCM", "FCM registration failed", error) }
  }

  override fun pushMessagingSink(): PushMessagingSink {
    return appComponent.pushMessagingSink()
  }
}
