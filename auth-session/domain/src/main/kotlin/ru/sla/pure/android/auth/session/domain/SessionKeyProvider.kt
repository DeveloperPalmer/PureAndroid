package ru.sla.pure.android.auth.session.domain

import kotlinx.coroutines.flow.Flow
import ru.sla.pure.android.auth.session.domain.entity.SessionKey

interface SessionKeyProvider {
  /**
   * Возвращает ключ активной сессии, если он существует.
   */
  suspend fun readKey(): SessionKey?

  /**
   * Сообщает об изменениях ключа активной сессии.
   */
  fun key(): Flow<SessionKey?>
}
