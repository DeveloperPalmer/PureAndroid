package ru.sla.pure.android.pushmessages.data

interface PushMessagingSink {
  suspend fun onNewRawMessage(message: RawMessage)
  fun onRegistered(installationId: String)
}
