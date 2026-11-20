package ru.sla.pure.android.pushmessages.android

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.pushmessages.domain.PushMessagesRepository
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage

fun configureNotificationDisplay(
  context: Context,
  scope: CoroutineScope,
  pushMessagesRepository: PushMessagesRepository
) {
  val manager = context.getSystemService(NotificationManager::class.java) ?: return
  scope.launch(start = CoroutineStart.UNDISPATCHED) {
    pushMessagesRepository.messages.collect { result ->
      result
        .onSuccess { message -> manager.display(context, message) }
        .onFailure { error -> Log.w("PureFCM", "Invalid push message", error) }
    }
  }
}

private fun NotificationManager.display(context: Context, message: PushMessage) {
  val channel = when (message.channel) {
    PushMessage.Channel.Default -> {
      NotificationChannel(
        "general",
        context.getString(R.string.main_notification_channel_title),
        NotificationManager.IMPORTANCE_HIGH,
      )
    }
  }
  createNotificationChannel(channel)

  if (SDK_INT >= TIRAMISU && context.checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
    return
  }

  val contentIntent = createMainScreenPendingIntent(context, message) ?: return
  val notification = Notification.Builder(context, channel.id)
    .setSmallIcon(R.drawable.ic_notification)
    .setContentTitle(message.title)
    .setContentText(message.description)
    .setStyle(Notification.BigTextStyle().bigText(message.description))
    .setAutoCancel(true)
    .setContentIntent(contentIntent)
    .build()
  notify(message.id.toInt(), notification)
}

private fun createMainScreenPendingIntent(context: Context, message: PushMessage): PendingIntent? {
  val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
  intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
  message.buildIntentExtras()
    .onSuccess { bundle -> intent.putExtras(bundle) }
    .onFailure { error -> Log.w("PureFCM", "Opening launcher without push extras", error) }
  return PendingIntent.getActivity(
    context,
    message.id.toInt(),
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
  )
}
