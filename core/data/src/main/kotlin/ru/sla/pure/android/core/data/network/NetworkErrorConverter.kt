package ru.sla.pure.android.core.data.network

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.sla.pure.android.core.data.network.entity.ApiErrorBody
import ru.sla.pure.android.core.data.network.mapper.toApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

internal fun convertNetworkError(error: Throwable): Throwable = when (error) {
  is HttpException -> readErrorBody(error).toApiError(error.code())
  is ConnectException,
  is UnknownHostException -> ConnectivityError.NoConnection(error)
  is SocketTimeoutException -> ConnectivityError.TimeOut(error)
  is SSLException -> ConnectivityError.SSLError(error)
  else -> error
}

private fun readErrorBody(error: HttpException): ApiErrorBody? {
  return try {
    error.response()
      ?.errorBody()
      ?.use { body -> ServerJson.decodeFromString<ApiErrorBody>(body.string()) }
  } catch (_: SerializationException) {
    null
  } catch (_: IllegalArgumentException) {
    null
  } catch (_: IOException) {
    null
  }
}
