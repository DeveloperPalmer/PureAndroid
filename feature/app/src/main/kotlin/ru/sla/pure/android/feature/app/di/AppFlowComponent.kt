package ru.sla.pure.android.feature.app.di

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.app.AppNavigator
import ru.sla.pure.android.feature.login.data.di.LoginComponent
import ru.sla.pure.android.feature.main.routing.MainFlow

@GraphExtension(AppFlowScope::class)
interface AppFlowComponent {
  @Binds
  val AppNavigator.bindNavigator: Navigator

  @Binds
  val MainFlow.bindFlowEventSink: FlowEventSink

  fun appNavigator(): AppNavigator

  @Multibinds(allowEmpty = true)
  fun entryProviderInstallers(): Set<EntryProviderInstaller>

  fun loginFactory(): LoginComponent.Factory

  @ContributesTo(ForegroundScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): AppFlowComponent
  }
}
