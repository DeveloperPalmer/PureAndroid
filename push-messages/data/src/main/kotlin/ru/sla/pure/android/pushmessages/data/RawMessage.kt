package ru.sla.pure.android.pushmessages.data

data class RawMessage(
  val data: Map<String, String>,
  val messageId: String?
)
