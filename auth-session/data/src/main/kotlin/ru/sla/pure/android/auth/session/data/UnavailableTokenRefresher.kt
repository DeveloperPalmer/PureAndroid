package ru.sla.pure.android.auth.session.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import ru.sla.pure.android.auth.session.domain.TokenRefresher
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.RefreshRejectedException
import ru.sla.pure.android.auth.session.domain.entity.RefreshToken

/**
 * Отказывает всегда: обменять refresh-токен не на что, пока в API нет такой операции.
 *
 * Это не заглушка ради компиляции, а единственный честный ответ. Раз протухший access обновить
 * нечем, сессия закончилась — и сказать об этом отказом лучше, чем сделать вид, что обновление
 * прошло.
 *
 * Заменяется реализацией, вызывающей операцию обновления, как только та появится.
 */
@ContributesBinding(AppScope::class)
class UnavailableTokenRefresher @Inject constructor() : TokenRefresher {
  override suspend fun refresh(token: RefreshToken): Nothing {
    throw RefreshRejectedException(cause = null)
  }
}
