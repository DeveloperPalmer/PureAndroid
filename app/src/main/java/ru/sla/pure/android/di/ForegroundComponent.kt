package ru.sla.pure.android.di

import android.app.Activity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.app.routing.NavigationHostState
import ru.sla.pure.android.core.domain.di.ActivityContext
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.deeplink.PendingDeepLink
import ru.sla.pure.android.feature.app.routing.AppFlow

@GraphExtension(ForegroundScope::class)
interface ForegroundComponent {
  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(
      @Provides
      @ActivityContext
      activity: Activity,
      @Provides
      pendingDeepLink: PendingDeepLink,
    ): ForegroundComponent
  }

  @Multibinds(allowEmpty = true)
  fun entryProviderInstallers(): Set<EntryProviderInstaller>

  fun navigationHostState(): NavigationHostState
}

@ContributesTo(ForegroundScope::class)
interface ForegroundModule {
  @Provides
  @SingleIn(ForegroundScope::class)
  fun provideNavigationHostState(pendingDeepLink: PendingDeepLink): NavigationHostState {
    return NavigationHostState(
      root = AppFlow.navKey,
      pendingDeepLink = pendingDeepLink
    )
  }
}
