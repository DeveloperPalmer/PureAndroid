package ru.sla.pure.android.core.data.network

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class NetworkErrorConverterTest {
  @Test
  @DisplayName("Ошибки HTTP сохраняют статус и тело ошибки API, включая ответ 401")
  fun `HTTP errors preserve status and API body`() {
    for (status in listOf(400, 401, 500)) {
      val error = httpError(status, """{"code":"request_failed","error":"Request failed"}""")

      assertEquals(ApiError(status, "request_failed", "Request failed"), convertNetworkError(error))
    }
  }

  @Test
  @DisplayName("Код ошибки API в ответах Rick and Morty необязателен")
  fun `API error code is optional`() {
    val error = httpError(404, """{"error":"Character not found"}""")

    assertEquals(ApiError(404, null, "Character not found"), convertNetworkError(error))
  }

  @Test
  @DisplayName("Статус HTTP сохраняется при пустом или некорректном теле ошибки")
  fun `invalid or empty HTTP error bodies preserve status`() {
    for (body in listOf("", "<html>Bad gateway</html>", "{}")) {
      assertEquals(ApiError(502, null, null), convertNetworkError(httpError(502, body)))
    }
  }

  @Test
  @DisplayName("Ошибки соединения сохраняют причину и различают таймаут и SSL")
  fun `connectivity failures preserve cause and distinguish timeout and SSL`() {
    val failures = listOf(
      ConnectException() to ConnectivityError.NoConnection::class.java,
      UnknownHostException() to ConnectivityError.NoConnection::class.java,
      SocketTimeoutException() to ConnectivityError.TimeOut::class.java,
      SSLException("handshake failed") to ConnectivityError.SSLError::class.java,
    )

    for ((cause, expectedType) in failures) {
      val error = convertNetworkError(cause)

      assertInstanceOf(expectedType, error)
      assertSame(cause, error.cause)
    }
  }

  @Test
  @DisplayName("Ошибки декодирования и неизвестные ошибки ввода-вывода не считаются сбоями соединения")
  fun `decoding and unknown IO errors are not connectivity failures`() {
    val errors = listOf(
      SerializationException("invalid response"),
      IOException("unexpected IO failure"),
      IllegalStateException("unexpected failure"),
      CancellationException("cancelled"),
      ApiError(500, null, null),
      ConnectivityError.NoConnection(null),
    )

    for (error in errors) {
      assertSame(error, convertNetworkError(error))
    }
  }

  private fun httpError(status: Int, body: String): HttpException {
    return HttpException(Response.error<Any>(status, body.toResponseBody("application/json".toMediaType())))
  }
}
