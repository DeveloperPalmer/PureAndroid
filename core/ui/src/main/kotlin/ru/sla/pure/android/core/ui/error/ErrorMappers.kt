package ru.sla.pure.android.core.ui.error

import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.UiError

fun baseErrorMapper(error: Throwable): UiError {
  val mapper = composeErrorMapper(
    ::connectionErrorMapper,
    ::serverErrorMapper,
    defaultMapper = ::defaultErrorMapper,
  )
  return mapper(error)
}

fun connectionErrorMapper(error: Throwable): UiError? {
  return if (error is ServerException.Unreachable) {
    UiError(
      messageResource = R.string.error_connection,
      cause = error,
    )
  } else {
    null
  }
}

fun serverErrorMapper(error: Throwable): UiError? {
  return if (error is ServerException) {
    UiError(
      messageResource = R.string.error_server,
      cause = error,
    )
  } else {
    null
  }
}

fun defaultErrorMapper(error: Throwable): UiError {
  return UiError(
    messageResource = R.string.error_unexpected,
    cause = error,
  )
}
