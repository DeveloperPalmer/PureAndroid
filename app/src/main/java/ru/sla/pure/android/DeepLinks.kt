package ru.sla.pure.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.deeplink.withBackStack
import ru.sla.pure.android.feature.main.routing.MainRoute
import ru.sla.pure.android.pushmessages.android.extractPushMessage
import ru.sla.pure.android.pushmessages.domain.entity.PushMessage

internal fun Intent.resolveDeepLink(): Result<List<NavKey>?> = runCatching {
  if (action == Intent.ACTION_VIEW && data != null) {
    Log.d("DEEPLINK", "resolveDeepLink with uri: ${data?.toString() ?: "no uri"}")
    val request = DeepLinkRequest(this)
    uriMatchers
      .mapNotNull { it.match(request) }
      .maxOrNull()
      ?.backStack
  } else {
    Log.d("DEEPLINK", "resolveDeepLink with extras: ${extras ?: "no extras"}")
    extras
      ?.pushNavKey()
      ?.let(::deepLinkBackStack)
  }
}

private fun Bundle.pushNavKey(): NavKey? {
  return when (val pushMessage = extractPushMessage()) {
    is PushMessage.OpenCharacter -> MainRoute.CharacterDetails(pushMessage.characterId)
    null -> null
  }
}

internal fun deepLinkBackStack(destination: NavKey): List<NavKey> {
  return when (destination) {
    is MainRoute.CharacterDetails -> listOf(MainRoute.Main, destination)
    else -> listOf(destination)
  }
}

private val uriMatchers: List<BackStackMatcher<NavKey, NavKey>> = listOf(
  UriDeepLinkMatcher(
    serializer = MainRoute.CharacterDetails.serializer(),
    uriPattern = DeepLinkUri("https://pure.android/characters/{characterId}"),
  )
).map { matcher -> matcher.withBackStack { deepLinkBackStack(it.key) } }
