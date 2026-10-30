package ru.sla.pure.android.core.domain.entity

sealed class ServerException(
  message: String,
  cause: Throwable?
) : RuntimeException(message, cause) {
  /** Запрос не доехал: разрыв, таймаут, недоступный хост. Повтор осмыслен. */
  class Unreachable(cause: Throwable?) : ServerException("server is unreachable", cause)

  /** Сервер отклонил запрос с HTTP 401. */
  class Unauthorized(cause: Throwable?) : ServerException("server rejected the session", cause)

  /**
   * Сервер назвал ошибку сам. [code] — то, по чему её различает вызывающий код;
   * `null` означает, что тело ответа кода не содержало.
   */
  class ApiError(
    val status: Int,
    val code: String?,
    val serverMessage: String?
  ) : ServerException(
      "server returned $status (code=${code ?: "none"}): ${serverMessage ?: "no message"}",
      null
    )

  /** Ответ не соответствует ожидаемой JSON-модели. */
  class Malformed(cause: Throwable?) : ServerException("server response did not decode", cause)
}
