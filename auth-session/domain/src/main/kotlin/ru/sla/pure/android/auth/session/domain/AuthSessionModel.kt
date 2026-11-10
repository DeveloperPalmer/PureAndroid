package ru.sla.pure.android.auth.session.domain

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.domain.ReactiveModel
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError

@SingleIn(AppScope::class)
class AuthSessionModel @Inject constructor(
  @ForScope(AppScope::class)
  parentScope: CoroutineScope,
  private val sessionRepository: AuthSessionRepository
) : ReactiveModel(parentScope) {
  val reset = task<Unit> {
    try {
      sessionRepository.reset(cleanupStorage = true)
    } catch (e: ApiError) {
      e.printStackTrace()
    } catch (e: ConnectivityError) {
      e.printStackTrace()
    }
  }

  val sessionState: Flow<AuthSessionState> = sessionRepository.key().flatMapLatest { key ->
    if (key == null) {
      flowOf(AuthSessionState.Inactive)
    } else {
      sessionRepository
        .tokens(key)
        .map { if (it != null) AuthSessionState.Active else AuthSessionState.Inactive }
    }
  }
}
