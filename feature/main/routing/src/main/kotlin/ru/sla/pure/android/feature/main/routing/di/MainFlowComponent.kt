package ru.sla.pure.android.feature.main.routing.di

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.FlowViewModelFactory
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.routing.MainFlow

@GraphExtension(MainScope::class)
interface MainFlowComponent : FlowComponent {
  @ContributesTo(AppFlowScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMainFlowComponent(): MainFlowComponent
  }

  @ForScope(MainScope::class)
  override fun coroutineScope(): CoroutineScope
}

@ContributesTo(MainScope::class)
interface MainFlowModule {
  @Binds
  val FlowViewModelFactory.bindMetroViewModelFactory: MetroViewModelFactory

  @Binds
  val MainFlow.bindFlowEventSink: FlowEventSink

  @Provides
  @SingleIn(MainScope::class)
  @ForScope(MainScope::class)
  fun provideCoroutineScope(): CoroutineScope = createCoroutineScope()
}
