package ru.sla.pure.android.lib.debugpanel.routing

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
import ru.sla.pure.android.core.resources.R

internal object DebugBroadcastNotification {
  const val ACTION_OPEN_DEBUG_PANEL = "ru.sla.pure.android.debug.OPEN_DEBUG_PANEL"

  private const val CHANNEL_ID = "debug_broadcast"
  private const val NOTIFICATION_ID = 1
  private const val NOTIFICATION_TAG = "debug_broadcast"

  fun show(context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java) ?: return
    val title = context.getString(R.string.debug_panel_title)
    manager.createNotificationChannel(
      NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_LOW)
    )
    if (SDK_INT >= TIRAMISU && context.checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
      return
    }
    val intent = Intent(ACTION_OPEN_DEBUG_PANEL).setPackage(context.packageName)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE
    )
    val notification = Notification.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .build()
    manager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
  }

  fun cancel(context: Context) {
    context.getSystemService(NotificationManager::class.java)?.cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
  }
}
