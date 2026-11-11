package ru.sla.pure.android.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import ru.sla.pure.android.app.routing.AppNavigator
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller

@GraphExtension(ForegroundScope::class)
interface ForegroundComponent {
  fun appNavigator(): AppNavigator

  @Multibinds(allowEmpty = true)
  fun entryProviderInstallers(): Set<EntryProviderInstaller>

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): ForegroundComponent
  }
}
