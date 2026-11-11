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
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.app.routing.AppFlow
import ru.sla.pure.android.feature.main.routing.di.MainFlowComponent

@GraphExtension(AppFlowScope::class)
interface AppFlowComponent : FlowComponent {
  @ContributesTo(ForegroundScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(
      @Provides navigator: Navigator
    ): AppFlowComponent
  }

  @Provides
  @SingleIn(AppFlowScope::class)
  @ForScope(AppFlowScope::class)
  fun provideCoroutineScope(): CoroutineScope {
    return createCoroutineScope()
  }

  @ForScope(AppFlowScope::class)
  override fun flow(): Flow

  @Binds
  @ForScope(AppFlowScope::class)
  val AppFlow.bindFlow: Flow

  @Binds
  @ForScope(AppFlowScope::class)
  val AppFlow.bindFlowEventSink: FlowEventSink

  @ForScope(AppFlowScope::class)
  override fun coroutineScope(): CoroutineScope

  fun mainFlowFactory(): MainFlowComponent.Factory
}
