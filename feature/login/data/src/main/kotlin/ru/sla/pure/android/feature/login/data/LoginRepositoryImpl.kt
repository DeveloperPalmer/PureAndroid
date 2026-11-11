package ru.sla.pure.android.feature.login.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.tasks.await
import ru.sla.pure.android.auth.session.domain.entity.AccessToken
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.RefreshToken
import ru.sla.pure.android.core.domain.entity.UserId
import ru.sla.pure.android.feature.login.domain.LoginRepository
import ru.sla.pure.android.feature.login.domain.di.LoginScope
import ru.sla.pure.android.feature.login.domain.entity.AuthResult
import ru.sla.pure.android.feature.login.domain.entity.GoogleAuthError
import ru.sla.pure.android.lib.google.authenticator.GoogleAuthenticator
import ru.sla.pure.android.lib.google.authenticator.SignInResult as GoogleSignInResult

@SingleIn(LoginScope::class)
@ContributesBinding(LoginScope::class)
class LoginRepositoryImpl @Inject constructor(
  private val googleAuthenticator: GoogleAuthenticator
) : LoginRepository {
  override suspend fun signInByGoogle(): AuthResult {
    return when (val signInResult = googleAuthenticator.auth()) {
      is GoogleSignInResult.Success -> {
        val user = signInResult.authResult
          .user
          ?: error("FirebaseAuth returned null user after Google sign-in")

        val firebaseIdToken = user.getIdToken(true)
          .await()
          .token
          ?: error("FirebaseAuth returned null ID token after Google sign-in")

        val userId = UserId(user.uid)

        AuthResult(
          userId = userId,
          tokens = AuthTokens(
            updatedAt = System.currentTimeMillis(),
            accessToken = AccessToken(firebaseIdToken),
            refreshToken = RefreshToken(firebaseIdToken)
          )
        )
      }
      is GoogleSignInResult.Error -> {
        throw GoogleAuthError.Authentication(signInResult.error)
      }
      is GoogleSignInResult.CancelledByUser -> {
        throw GoogleAuthError.CancelledByUser(cause = null)
      }
    }
  }
}
