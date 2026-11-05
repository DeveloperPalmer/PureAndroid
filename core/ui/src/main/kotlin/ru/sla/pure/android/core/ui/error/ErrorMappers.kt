package ru.sla.pure.android.core.ui.error

import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.UiError

fun baseErrorMapper(error: Throwable): UiError {
  val mapper = composeErrorMapper(
    ::connectionErrorMapper,
    ::apiErrorMapper,
    defaultMapper = ::defaultErrorMapper,
  )
  return mapper(error)
}

fun connectionErrorMapper(error: Throwable): UiError? {
  return if (error is ConnectivityError) {
    UiError(
      messageResource = R.string.error_connection,
      cause = error,
    )
  } else {
    null
  }
}

fun apiErrorMapper(error: Throwable): UiError? {
  return if (error is ApiError) {
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
