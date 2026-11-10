package ru.sla.pure.android.feature.login.domain.entity

import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.core.domain.entity.UserId

data class AuthResult(
  val userId: UserId,
  val tokens: AuthTokens
)
