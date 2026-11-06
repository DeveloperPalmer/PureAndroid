package ru.sla.pure.android.feature.app.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.feature.app.AppNavigator

@GraphExtension(AppFlowScope::class)
interface AppFlowComponent {
  @Binds
  val AppNavigator.bindNavigator: Navigator

  fun appNavigator(): AppNavigator

  @Multibinds(allowEmpty = true)
  fun entryProviderInstallers(): Set<EntryProviderInstaller>

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): AppFlowComponent
  }
}
