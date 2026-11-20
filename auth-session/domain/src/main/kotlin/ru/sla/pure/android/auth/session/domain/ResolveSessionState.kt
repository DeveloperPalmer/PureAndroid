package ru.sla.pure.android.auth.session.domain

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState

/** При ошибке чтения сессии приложение запрашивает вход повторно. */
suspend fun Flow<AuthSessionState>.firstOrInactive(): AuthSessionState = try {
  first()
} catch (exception: CancellationException) {
  throw exception
} catch (_: Exception) {
  AuthSessionState.Inactive
}
