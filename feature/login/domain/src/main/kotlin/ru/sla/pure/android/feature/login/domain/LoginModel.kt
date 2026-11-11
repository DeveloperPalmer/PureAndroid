package ru.sla.pure.android.feature.login.domain

import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.auth.session.domain.AuthSessionRepository
import ru.sla.pure.android.core.domain.ReactiveModel
import ru.sla.pure.android.feature.login.domain.di.LoginScope

@SingleIn(LoginScope::class)
class LoginModel @Inject constructor(
  @ForScope(LoginScope::class)
  parentScope: CoroutineScope,
  private val authSessionRepository: AuthSessionRepository,
  private val loginRepository: LoginRepository
) : ReactiveModel(parentScope) {
  suspend fun saveSession() {
    val result = loginRepository.signInByGoogle()
    authSessionRepository.startNew(
      userId = result.userId,
      tokens = result.tokens
    )
  }
}
