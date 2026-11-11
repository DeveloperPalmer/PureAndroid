package ru.sla.pure.android.feature.main.routing.di

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.core.ui.FlowEventSink
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.routing.MainFlow

/**
 * Граф флоу «Главная» получает собственный навигатор и наследует граф AppFlow и хранит свои ViewModel в MainScope.
 */
@GraphExtension(MainScope::class)
interface MainFlowComponent : FlowComponent {
  @ContributesTo(AppFlowScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMainFlowComponent(
      @Provides @ForScope(MainScope::class) navigator: Navigator,
    ): MainFlowComponent
  }

  @ForScope(MainScope::class)
  override fun flow(): Flow

  @Binds
  @ForScope(MainScope::class)
  val MainFlow.bindFlow: Flow

  @Binds
  val MainFlow.bindFlowEventSink: FlowEventSink

  @ForScope(MainScope::class)
  override fun coroutineScope(): CoroutineScope

  @Provides
  @SingleIn(MainScope::class)
  @ForScope(MainScope::class)
  fun provideCoroutineScope(): CoroutineScope {
    return createCoroutineScope()
  }
}
