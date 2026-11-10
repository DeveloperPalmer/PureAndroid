package ru.sla.pure.android.auth.session.domain.entity

data class AuthTokens(
  val accessToken: AccessToken,
  val refreshToken: RefreshToken,
  val updatedAt: Long
)

@JvmInline
value class AccessToken(val value: String)

@JvmInline
value class RefreshToken(val value: String)
