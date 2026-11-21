package ru.sla.pure.android.feature.app.routing.di

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.app.routing.AppFlow
import ru.sla.pure.android.feature.login.routing.di.LoginFlowComponent
import ru.sla.pure.android.feature.main.routing.di.MainFlowComponent

@GraphExtension(AppFlowScope::class)
interface AppFlowComponent : FlowComponent {
  @ContributesTo(ForegroundScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): AppFlowComponent
  }

  @ForScope(AppFlowScope::class)
  override fun coroutineScope(): CoroutineScope

  fun loginFlowComponentFactory(): LoginFlowComponent.Factory
  fun mainFlowComponentFactory(): MainFlowComponent.Factory
}

@ContributesTo(AppFlowScope::class)
interface AppFlowModule {
  @Binds
  @ForScope(AppFlowScope::class)
  val AppFlow.bindFlowEventSink: FlowEventSink

  @Provides
  @SingleIn(AppFlowScope::class)
  @ForScope(AppFlowScope::class)
  fun provideCoroutineScope(): CoroutineScope = createCoroutineScope()
}
