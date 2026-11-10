package ru.sla.pure.android.feature.login.domain.entity

sealed interface GoogleAuthError {
  data class CancelledByUser(override val cause: Throwable?) : RuntimeException()
  data class Authentication(override val cause: Throwable?) : RuntimeException()
}
