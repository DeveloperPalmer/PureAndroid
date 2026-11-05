package ru.sla.pure.android.core.data.network.mapper

import ru.sla.pure.android.core.data.network.entity.ApiErrorBody
import ru.sla.pure.android.core.domain.entity.ApiError

internal fun ApiErrorBody?.toApiError(status: Int): ApiError {
  return ApiError(
    status = status,
    code = this?.code,
    description = this?.message,
  )
}
