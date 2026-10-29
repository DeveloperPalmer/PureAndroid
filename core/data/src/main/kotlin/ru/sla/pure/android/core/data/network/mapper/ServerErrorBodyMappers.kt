package ru.sla.pure.android.core.data.network.mapper

import ru.sla.pure.android.core.data.network.entity.ServerErrorBody
import ru.sla.pure.android.core.domain.entity.ServerException

internal fun ServerErrorBody?.toServerException(status: Int): ServerException {
    if (status == 401) {
        return ServerException.Unauthorized(cause = null)
    }
    return ServerException.ApiError(
        status = status,
        code = this?.code,
        serverMessage = this?.message,
    )
}
