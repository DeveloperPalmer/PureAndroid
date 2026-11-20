package ru.sla.pure.android.pushmessages.data.di

import ru.sla.pure.android.pushmessages.data.PushMessagingSink

interface PushMessagingSinkHolder {
  fun pushMessagingSink(): PushMessagingSink
}
