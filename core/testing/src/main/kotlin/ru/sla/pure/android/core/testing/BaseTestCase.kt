package ru.sla.pure.android.core.testing

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
open class BaseTestCase protected constructor() : TestCase(Kaspresso.Builder.withComposeSupport()) {
  protected fun runScreenTest(
    testTimeout: Duration = 60.seconds,
    scenario: AndroidComposeUiTest<ComponentActivity>.(TestContext<Unit>) -> Unit,
  ) = runAndroidComposeUiTest<ComponentActivity>(testTimeout = testTimeout) {
    val compose = this
    // Keep the Activity alive while Kaspresso runs its failure interceptors.
    this@BaseTestCase.run { compose.scenario(this) }
  }
}
