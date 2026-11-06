package ru.sla.pure.android.testing

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import java.util.concurrent.TimeUnit

/** Per-test HTTP boundary shared by all feature E2E fixtures. */
class TestBackend {
  private val server = MockWebServer()

  val baseUrl: String
    get() = server.url("/").toString()

  val requestCount: Int
    get() = server.requestCount

  fun start() {
    server.start()
  }

  fun enqueueJson(body: String, code: Int = 200) {
    require(code in 100..599) { "Unexpected HTTP status: $code" }
    server.enqueue(
      MockResponse.Builder()
        .code(code)
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .body(body)
        .build()
    )
  }

  fun takeRequestTarget(timeoutSeconds: Long = 5): String {
    val request = server.takeRequest(timeoutSeconds, TimeUnit.SECONDS)
    return checkNotNull(request) { "No HTTP request in $timeoutSeconds seconds" }.target
  }

  fun close() {
    server.close()
  }
}
