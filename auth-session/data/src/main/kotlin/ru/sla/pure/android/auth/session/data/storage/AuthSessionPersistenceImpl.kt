package ru.sla.pure.android.auth.session.data.storage

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.sla.pure.android.auth.session.domain.entity.AccessToken
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.RefreshToken
import ru.sla.pure.android.auth.session.domain.entity.SessionKey
import ru.sla.pure.android.core.domain.entity.UserId
import ru.sla.pure.android.core.domain.mapDistinctNotNullChanges
import ru.sla.pure.android.core.domain.randomUuid
import ru.sla.pure.android.database.dao.SettingsDao
import ru.sla.pure.android.database.entity.SettingsEntity

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthSessionPersistenceImpl @Inject constructor(
  private val settingsDao: SettingsDao
) : AuthSessionPersistence {
  override fun <R> produceUserId(transform: (UserId) -> Flow<R>): Flow<R> {
    return key()
      .filterNotNull()
      .mapDistinctNotNullChanges { readUserId(it) }
      .flatMapLatest { transform(it) }
  }

  override suspend fun saveTokens(key: SessionKey, tokens: AuthTokens) {
    settingsDao.insertOrReplace(
      SettingsEntity(
        key = buildTokensKey(key),
        value = serializeTokens(tokens)
      )
    )
  }

  override fun tokens(key: SessionKey): Flow<AuthTokens?> {
    return settingsDao
      .observe(buildTokensKey(key))
      .map { dbRecord -> dbRecord?.let { deserializeTokens(it) } ?: return@map null }
  }

  override suspend fun readTokens(key: SessionKey): AuthTokens? {
    val dbRecord = settingsDao.select(buildTokensKey(key))
    return dbRecord?.let { deserializeTokens(it) }
  }

  override suspend fun saveUserId(key: SessionKey, userId: UserId) {
    settingsDao.insertOrReplace(
      SettingsEntity(
        key = buildUserIdKey(key),
        value = userId.value
      )
    )
  }

  override suspend fun readUserId(key: SessionKey): UserId? {
    return settingsDao
      .select(buildUserIdKey(key))
      ?.let(::UserId)
  }

  override suspend fun deleteUserId(key: SessionKey) {
    settingsDao.delete(buildUserIdKey(key))
  }

  private fun buildTokensKey(sessionKey: SessionKey): SettingsEntity.Key {
    return SettingsEntity.Key(PREF_KEY_TOKENS + PREF_KEY_SEPARATOR + sessionKey.value)
  }

  private fun buildUserIdKey(sessionKey: SessionKey): SettingsEntity.Key {
    return SettingsEntity.Key(PREF_KEY_USER_ID + PREF_KEY_SEPARATOR + sessionKey.value)
  }

  private fun serializeTokens(tokens: AuthTokens): String {
    return tokens.accessToken.value + PREF_KEY_SEPARATOR +
      tokens.refreshToken.value + PREF_KEY_SEPARATOR +
      tokens.updatedAt
  }

  private fun deserializeTokens(dbRecord: String): AuthTokens {
    val tokens = dbRecord.split(PREF_KEY_SEPARATOR)
    check(tokens.size == 3) { "expected tokens pref value to consist of 3 parts, got ${tokens.size}" }
    return AuthTokens(
      AccessToken(tokens[0]),
      RefreshToken(tokens[1]),
      updatedAt = tokens[2].toLong()
    )
  }

  override suspend fun deleteTokens(key: SessionKey) {
    settingsDao.delete(buildTokensKey(key))
  }

  override suspend fun <T> withKey(body: suspend AuthSessionPersistence.(key: SessionKey) -> T): T {
    return body(readKey() ?: error("expected active session key"))
  }

  override suspend fun generateKeyAndSetActive(): SessionKey {
    val key = SessionKey(randomUuid())
    writeActiveKey(key)
    return key
  }

  override suspend fun clearKey() {
    deleteActiveKey()
  }

  override fun key(): Flow<SessionKey?> {
    return settingsDao
      .observe(PREF_KEY_ACTIVE_SESSION_KEY)
      .map { it?.let(::SessionKey) }
  }

  override suspend fun readKey(): SessionKey? {
    return settingsDao
      .select(PREF_KEY_ACTIVE_SESSION_KEY)
      ?.let { SessionKey(it) }
  }

  private suspend fun writeActiveKey(key: SessionKey) {
    settingsDao.insertOrReplace(
      SettingsEntity(
        key = PREF_KEY_ACTIVE_SESSION_KEY,
        value = key.value
      )
    )
  }

  private suspend fun deleteActiveKey() {
    settingsDao.delete(PREF_KEY_ACTIVE_SESSION_KEY)
  }
}

private val PREF_KEY_ACTIVE_SESSION_KEY = SettingsEntity.Key("session_key")
private const val PREF_KEY_TOKENS = "tokens"
private const val PREF_KEY_USER_ID = "user_id"
private const val PREF_KEY_SEPARATOR = "::::"
