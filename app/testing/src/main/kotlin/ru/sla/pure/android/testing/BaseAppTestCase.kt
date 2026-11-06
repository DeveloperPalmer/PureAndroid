package ru.sla.pure.android.testing

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.TestResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import ru.sla.pure.android.core.testing.BaseTestCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
open class BaseAppTestCase protected constructor() : BaseTestCase() {
  protected lateinit var backend: TestBackend
    private set

  private lateinit var app: AppUnderTest

  @BeforeEach
  fun installApp() {
    backend = TestBackend()
    backend.start()
    app = InstrumentationRegistry.getInstrumentation()
      .targetContext.applicationContext as AppUnderTest
    app.install(backend.baseUrl)
  }

  @AfterEach
  fun resetApp() {
    if (::app.isInitialized) app.reset()
    if (::backend.isInitialized) backend.close()
  }

  protected fun runAppTest(
    testTimeout: Duration = 60.seconds,
    scenario: AndroidComposeUiTest<ComponentActivity>.(TestContext<Unit>) -> Unit,
  ): TestResult = runAndroidComposeUiTest<ComponentActivity>(
    activityClass = mainActivityClass(),
    testTimeout = testTimeout,
  ) {
    val compose = this
    // Keep the real Activity alive while Kaspresso runs its failure interceptors.
    this@BaseAppTestCase.run { compose.scenario(this) }
  }

  @Suppress("UNCHECKED_CAST")
  private fun mainActivityClass(): Class<ComponentActivity> =
    app.mainActivityClass() as Class<ComponentActivity>
}
