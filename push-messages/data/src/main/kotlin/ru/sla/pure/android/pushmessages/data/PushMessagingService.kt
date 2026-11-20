package ru.sla.pure.android.pushmessages.data

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sla.pure.android.buildconfig.BuildConfigProvider
import ru.sla.pure.android.buildconfig.BuildType

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PushMessagingService @Inject constructor(
  private val buildConfigProvider: BuildConfigProvider,
) : PushMessagingSink {
  private val rawMessages = MutableSharedFlow<RawMessage>()
  private val currentInstallationId = MutableStateFlow<String?>(null)

  val messages = rawMessages.asSharedFlow()
  val installationId = currentInstallationId.asStateFlow()

  override suspend fun onNewRawMessage(message: RawMessage) {
    if (buildConfigProvider.buildType == BuildType.Debug) {
      Log.d("PureFCM", "FCM message received: id=${message.messageId ?: "no messageId"}, dataKeys=${message.data.keys}")
    }
    rawMessages.emit(message)
  }

  override fun onRegistered(installationId: String) {
    if (buildConfigProvider.buildType == BuildType.Debug) {
      Log.d("PureFCM", "Firebase Installation ID: $installationId")
    }
    currentInstallationId.value = installationId
  }
}
