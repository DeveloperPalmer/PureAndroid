package ru.sla.pure.android.feature.login.domain

import ru.sla.pure.android.feature.login.domain.entity.AuthResult

interface LoginRepository {
  suspend fun signInByGoogle(): AuthResult
}
