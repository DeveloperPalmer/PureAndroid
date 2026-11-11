package ru.sla.pure.android.lib.google.authenticator

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import ru.sla.pure.android.buildconfig.BuildConfigProvider
import ru.sla.pure.android.core.domain.di.ActivityContext

class GoogleAuthenticator @Inject constructor(
  @ActivityContext
  private val activity: Activity,
  private val buildConfig: BuildConfigProvider
) {
  private val credentialManager = CredentialManager.create(activity)
  private val firebaseAuth = Firebase.auth

  @Suppress("TooGenericExceptionCaught")
  suspend fun auth(): SignInResult {
    return try {
      SignInResult.Success(signInInternal(buildConfig.googleWebClientId))
    } catch (_: GetCredentialCancellationException) {
      SignInResult.CancelledByUser
    } catch (e: CancellationException) {
      throw e
    } catch (exception: Exception) {
      SignInResult.Error(exception)
    }
  }

  suspend fun signOut() {
    firebaseAuth.signOut()
    try {
      val clearRequest = ClearCredentialStateRequest()
      credentialManager.clearCredentialState(clearRequest)
    } catch (e: ClearCredentialException) {
      Log.w("GoogleAuthenticator", "Не удалось сбросить состояние учётных данных", e)
      throw e
    }
  }

  private suspend fun signInInternal(clientId: String): AuthResult {
    val googleAuthResult = runSuspendCatching {
      val signInWithGoogle = GetSignInWithGoogleOption.Builder(clientId)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogle)
        .build()
      credentialManager.getCredential(
        context = activity,
        request = request
      )
    }.getOrElse { e ->
      if (e is GetCredentialCancellationException) {
        throw e
      } else {
        credentialManager.getCredential(
          request = createGoogleIdRequest(clientId),
          context = activity
        )
      }
    }

    if (googleAuthResult.credential is CustomCredential) {
      val googleId = GoogleIdTokenCredential.createFrom(googleAuthResult.credential.data)
      val credential = GoogleAuthProvider.getCredential(googleId.idToken, null)

      val authResult = firebaseAuth.signInWithCredential(credential).await()
      if (authResult.user?.isAnonymous == true) error("Randomly generated e-mail")
      return authResult
    } else {
      throw GoogleIdTokenParsingException()
    }
  }

  private fun createGoogleIdRequest(clientId: String): GetCredentialRequest {
    val googleId = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(clientId)
      .build()

    return GetCredentialRequest.Builder().addCredentialOption(googleId).build()
  }
}

sealed interface SignInResult {
  data object CancelledByUser : SignInResult
  data class Success(val authResult: AuthResult) : SignInResult
  data class Error(val error: Throwable) : SignInResult
}
