package ru.sla.pure.android.auth.session.data.storage

import kotlinx.coroutines.flow.Flow
import ru.sla.pure.android.auth.session.domain.SessionKeyProvider
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.SessionKey
import ru.sla.pure.android.core.domain.entity.UserId

interface AuthSessionPersistence : SessionKeyProvider {
  suspend fun saveTokens(key: SessionKey, tokens: AuthTokens)
  suspend fun readTokens(key: SessionKey): AuthTokens?
  suspend fun deleteTokens(key: SessionKey)

  fun <R> produceUserId(transform: (UserId) -> Flow<R>): Flow<R>
  suspend fun saveUserId(key: SessionKey, userId: UserId)
  suspend fun readUserId(key: SessionKey): UserId?
  suspend fun deleteUserId(key: SessionKey)

  fun tokens(key: SessionKey): Flow<AuthTokens?>

  /**
   * Выполняет блок кода, передавая ему ключ текущей активной сессии.
   *
   * @throws IllegalStateException если активного ключа нет.
   */
  suspend fun <T> withKey(body: suspend AuthSessionPersistence.(key: SessionKey) -> T): T

  /**
   * Генерирует новый [SessionKey] и делает его активным.
   *
   * [SessionKey] сохраняется на протяжении одной сессии: от входа до выхода.
   * Обновление токенов не меняет ключ.
   */
  suspend fun generateKeyAndSetActive(): SessionKey

  /**
   * Возвращает ключ активной сессии, если он существует.
   */
  override suspend fun readKey(): SessionKey?

  /**
   * Сообщает об изменениях ключа активной сессии.
   */
  override fun key(): Flow<SessionKey?>

  /**
   * Удаляет ключ активной сессии.
   */
  suspend fun clearKey()
}
