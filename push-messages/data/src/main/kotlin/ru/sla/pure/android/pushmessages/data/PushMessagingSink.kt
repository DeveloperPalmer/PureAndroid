package ru.sla.pure.android.pushmessages.data

interface PushMessagingSink {
  fun onNewRawMessage(message: RawMessage)
  fun onRegistered(installationId: String)
}
