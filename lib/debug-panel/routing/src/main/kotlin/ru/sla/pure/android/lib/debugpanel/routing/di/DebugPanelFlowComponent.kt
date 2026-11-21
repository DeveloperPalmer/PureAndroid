package ru.sla.pure.android.lib.debugpanel.routing.di

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.FlowViewModelFactory
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.lib.debugpanel.domain.di.DebugPanelScope
import ru.sla.pure.android.lib.debugpanel.routing.DebugPanelFlow

@GraphExtension(DebugPanelScope::class)
interface DebugPanelFlowComponent : FlowComponent {
  @ContributesTo(ForegroundScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createDebugPanelFlowComponent(): DebugPanelFlowComponent
  }

  @ForScope(DebugPanelScope::class)
  override fun coroutineScope(): CoroutineScope
}

@ContributesTo(DebugPanelScope::class)
interface DebugPanelFlowModule {
  @Binds
  val FlowViewModelFactory.bindMetroViewModelFactory: MetroViewModelFactory

  @Binds
  val DebugPanelFlow.bindFlowEventSink: FlowEventSink

  @Provides
  @SingleIn(DebugPanelScope::class)
  @ForScope(DebugPanelScope::class)
  fun provideCoroutineScope(): CoroutineScope = createCoroutineScope()
}
