package ru.sla.pure.android.testing

import androidx.activity.ComponentActivity

/** App-specific capabilities exposed without exposing application types to feature tests. */
interface AppUnderTest {
  fun install(baseUrl: String)

  fun reset()

  fun mainActivityClass(): Class<out ComponentActivity>
}
