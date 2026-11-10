package ru.sla.pure.android.auth.session.data.storage

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.sla.pure.android.auth.session.data.AccessTokenRefresher
import ru.sla.pure.android.auth.session.domain.AuthSessionRepository
import ru.sla.pure.android.auth.session.domain.entity.AccessToken
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.SessionKey
import ru.sla.pure.android.core.domain.entity.UserId
import ru.sla.pure.android.database.CharacterDatabase

@ContributesBinding(AppScope::class)
class AuthSessionRepositoryImpl @Inject constructor(
  private val characterDatabase: CharacterDatabase,
  private val authSessionPersistence: AuthSessionPersistence,
  private val accessTokenRefresher: AccessTokenRefresher,
) : AuthSessionRepository {
  override suspend fun refresh(staleAccessToken: AccessToken): AuthTokens? {
    return accessTokenRefresher.refresh(staleAccessToken)
  }

  override suspend fun readTokens(key: SessionKey): AuthTokens? {
    return authSessionPersistence.readTokens(key)
  }

  override fun tokens(key: SessionKey): Flow<AuthTokens?> {
    return authSessionPersistence.tokens(key)
  }

  override suspend fun <T> withKey(
    body: suspend AuthSessionRepository.(key: SessionKey) -> T,
  ): T {
    return body(readKey() ?: error("expected active session key"))
  }

  override suspend fun readKey(): SessionKey? {
    return authSessionPersistence.readKey()
  }

  override suspend fun readUserId(key: SessionKey): UserId? {
    return authSessionPersistence.readUserId(key)
  }

  override fun key(): Flow<SessionKey?> {
    return authSessionPersistence.key()
  }

  override suspend fun deleteTokens(key: SessionKey) {
    authSessionPersistence.deleteTokens(key)
    authSessionPersistence.deleteUserId(key)
  }

  override suspend fun startNew(tokens: AuthTokens, userId: UserId) {
    val key = authSessionPersistence.generateKeyAndSetActive()
    authSessionPersistence.saveTokens(key, tokens)
    authSessionPersistence.saveUserId(key, userId)
  }

  override suspend fun reset(cleanupStorage: Boolean) {
    val key = authSessionPersistence.readKey() ?: return
    authSessionPersistence.deleteTokens(key)
    authSessionPersistence.deleteUserId(key)
    if (cleanupStorage) {
      cleanupStorage(key)
    }
    authSessionPersistence.clearKey()
  }

  override suspend fun cleanupStorage(key: SessionKey) {
    // Кэш персонажей общий для всех сессий, поэтому очищаем его целиком.
    withContext(Dispatchers.IO) {
      characterDatabase.clearAllTables()
    }
  }
}
