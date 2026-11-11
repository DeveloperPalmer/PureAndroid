package ru.sla.pure.android.feature.login.ui.screen.credentials

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.auth.session.domain.AuthSessionModel
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.login.domain.LoginModel
import ru.sla.pure.android.feature.login.domain.di.LoginScope
import ru.sla.pure.android.feature.login.domain.entity.GoogleAuthError
import ru.sla.pure.android.feature.login.ui.R
import ru.sla.pure.android.feature.login.ui.routing.FlowEvent

@ViewModelKey(CredentialsViewModel::class)
@ContributesIntoMap(LoginScope::class, binding<ViewModel>())
class CredentialsViewModel @Inject constructor(
  private val loginModel: LoginModel,
  sessionModel: AuthSessionModel,
  @ForScope(LoginScope::class)
  private val events: FlowEventSink,
) : BaseViewModel<CredentialsState, Nothing>() {
  override val container = orbitContainer<CredentialsState, Nothing>(
    initialState = CredentialsState(),
    buildSettings = { intentLaunchingDispatcher = { Dispatchers.Main.immediate } },
  ) {
    val session = sessionModel.sessionState
      .catch { emit(AuthSessionState.Inactive) }
      .first()
    if (session == AuthSessionState.Active) {
      events.sendEvent(FlowEvent.LoginSucceeded)
    }
  }

  fun onSignInByGoogle() = intent {
    if (state.googleInProgress) return@intent

    reduce { CredentialsState(googleInProgress = true) }
    try {
      loginModel.saveSession()
    } catch (exception: CancellationException) {
      throw exception
    } catch (_: GoogleAuthError.CancelledByUser) {
      reduce { CredentialsState() }
      return@intent
    } catch (_: Exception) {
      reduce { CredentialsState(message = R.string.credentials_sign_in_failed) }
      return@intent
    }

    events.sendEvent(FlowEvent.LoginSucceeded)
  }

  fun onSignInByEmail() = intent {
    if (!state.googleInProgress) {
      reduce { state.copy(message = R.string.credentials_email_unavailable) }
    }
  }
}

data class CredentialsState(
  val googleInProgress: Boolean = false,
  @param:StringRes val message: Int? = null,
)
