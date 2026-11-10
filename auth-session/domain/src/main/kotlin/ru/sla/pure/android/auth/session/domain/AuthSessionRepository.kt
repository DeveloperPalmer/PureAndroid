package ru.sla.pure.android.auth.session.domain

import kotlinx.coroutines.flow.Flow
import ru.sla.pure.android.auth.session.domain.entity.AccessToken
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.SessionKey
import ru.sla.pure.android.core.domain.entity.UserId

interface AuthSessionRepository {
  /**
   * Обменивает протухшую пару на свежую.
   *
   * @param staleAccessToken токен, с которым вызывающий получил 401. По нему видно, не обновил
   *   ли пару параллельный вызов, пока этот ждал: обновление идёт ровно одно на всех.
   * @return свежая пара либо `null`, если сессии уже нет.
   * @throws ru.sla.pure.android.auth.session.domain.entity.RefreshRejectedException если сервер отверг
   *   refresh-токен. К этому моменту ключ сессии уже стёрт.
   */
  suspend fun refresh(staleAccessToken: AccessToken): AuthTokens?

  /**
   * Начинает новую сессию авторизации с указанными токенами и идентификатором пользователя.
   */
  suspend fun startNew(tokens: AuthTokens, userId: UserId)

  /**
   * Сбрасывает сессию, удаляя сохранённые секреты и токены.
   * Ничего не делает, если активной сессии нет.
   *
   * Дополнительно очищает хранилище данных, если [cleanupStorage] равен true и ключ сессии доступен.
   */
  suspend fun reset(cleanupStorage: Boolean)

  /**
   * Сбрасывает только токены, сохраняя секреты. Например, при установленном PIN-коде
   * сессию можно обновить после вызова этого метода.
   */
  suspend fun deleteTokens(key: SessionKey)

  suspend fun readTokens(key: SessionKey): AuthTokens?
  fun tokens(key: SessionKey): Flow<AuthTokens?>

  suspend fun readUserId(key: SessionKey): UserId?

  /**
   * Выполняет блок кода, передавая ему ключ текущей активной сессии.
   *
   * @throws IllegalStateException если активного ключа нет.
   */
  suspend fun <T> withKey(body: suspend AuthSessionRepository.(key: SessionKey) -> T): T

  /**
   * Возвращает ключ активной сессии, если он существует.
   */
  suspend fun readKey(): SessionKey?

  /**
   * Сообщает об изменениях ключа активной сессии.
   */
  fun key(): Flow<SessionKey?>

  /**
   * Очищает данные сессии [key] в памяти и базе данных.
   */
  suspend fun cleanupStorage(key: SessionKey)
}
