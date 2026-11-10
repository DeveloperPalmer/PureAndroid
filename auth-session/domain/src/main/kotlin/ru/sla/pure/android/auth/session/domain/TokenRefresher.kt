package ru.sla.pure.android.auth.session.domain

import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.RefreshToken

/**
 * Обмен refresh-токена на свежую пару — вторая операция тега `auth`.
 *
 * Отдельным портом, а не прямым вызовом сгенерированного клиента: этот вызов обязан уходить мимо
 * обновления токена, иначе отказ по 401 обновлял бы токен, чтобы обновить токен.
 */
fun interface TokenRefresher {
  /**
   * @throws ru.sla.pure.android.auth.session.domain.entity.RefreshRejectedException если сервер
   *   отверг refresh-токен.
   */
  suspend fun refresh(token: RefreshToken): AuthTokens
}
