package ru.sla.pure.android.core.data.network

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.sla.pure.android.core.data.network.entity.ServerErrorBody
import ru.sla.pure.android.core.data.network.mapper.toServerException
import ru.sla.pure.android.core.domain.entity.ServerException
import java.io.IOException

internal fun convertServerError(error: Throwable): Throwable = when (error) {
  is HttpException -> readErrorBody(error).toServerException(error.code())
  is IOException -> ServerException.Unreachable(error)
  is SerializationException -> ServerException.Malformed(error)
  else -> error
}

private fun readErrorBody(error: HttpException): ServerErrorBody? {
  return try {
    error.response()?.errorBody()?.use { body ->
      ServerJson.decodeFromString(ServerErrorBody.serializer(), body.string())
    }
  } catch (_: SerializationException) {
    null
  } catch (_: IllegalArgumentException) {
    null
  } catch (_: IOException) {
    null
  }
}
