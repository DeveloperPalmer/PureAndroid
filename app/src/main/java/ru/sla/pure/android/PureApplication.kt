package ru.sla.pure.android

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.createGraphFactory
import ru.sla.pure.android.di.AppComponent
import ru.sla.pure.android.di.ForegroundComponent
import ru.sla.pure.android.pushmessages.android.configureNotificationDisplay
import ru.sla.pure.android.pushmessages.data.PushMessagingSink
import ru.sla.pure.android.pushmessages.data.di.PushMessagingSinkHolder

/**
 * Точка входа DI: держит [appComponent] — корневой граф Metro. Он создаётся при первом обращении
 * и живёт до конца процесса
 */
open class PureApplication : Application(), PushMessagingSinkHolder {
  val appComponent: AppComponent by lazy { createGraphFactory<AppComponent.Factory>().create(this) }
  open val foregroundFactory: ForegroundComponent.Factory
    get() = appComponent.foregroundComponentFactory()

  override fun onCreate() {
    super.onCreate()
    configureNotificationDisplay(
      context = this,
      scope = appComponent.applicationScope(),
      pushMessagesRepository = appComponent.pushMessagesRepository()
    )
    FirebaseMessaging.getInstance()
      .register()
      .addOnFailureListener { error -> Log.e("PureFCM", "FCM registration failed", error) }
  }

  override fun pushMessagingSink(): PushMessagingSink {
    return appComponent.pushMessagingSink()
  }
}
