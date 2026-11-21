package ru.sla.pure.android.lib.debugpanel.routing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sla.pure.android.core.domain.di.ForegroundScope

@SingleIn(ForegroundScope::class)
class DebugPanelAdapter @Inject constructor() {
  private val openState = MutableStateFlow(false)
  val isOpen = openState.asStateFlow()

  private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (intent.action == DebugBroadcastNotification.ACTION_OPEN_DEBUG_PANEL) {
        Log.d(LOG_TAG, "Received ${intent.action ?: "no intent"}")
        openState.value = true
      }
    }
  }

  fun start(context: Context) {
    ContextCompat.registerReceiver(
      context,
      broadcastReceiver,
      IntentFilter(DebugBroadcastNotification.ACTION_OPEN_DEBUG_PANEL),
      ContextCompat.RECEIVER_NOT_EXPORTED
    )
    Log.d(LOG_TAG, "Receiver registered")
    DebugBroadcastNotification.show(context)
  }

  fun stop(context: Context) {
    DebugBroadcastNotification.cancel(context)
    context.unregisterReceiver(broadcastReceiver)
    Log.d(LOG_TAG, "Receiver unregistered")
  }

  internal fun close() {
    openState.value = false
  }

  private companion object {
    const val LOG_TAG = "DebugBroadcast"
  }
}
