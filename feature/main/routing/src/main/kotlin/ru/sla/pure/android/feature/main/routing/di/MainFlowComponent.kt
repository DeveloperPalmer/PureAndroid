package ru.sla.pure.android.feature.main.routing.di

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.routing.AppFlowScope
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.feature.main.domain.di.MainScope

/**
 * Граф флоу «Главная» наследует общие привязки AppFlow и хранит свои ViewModel в MainScope.
 */
@GraphExtension(MainScope::class)
interface MainFlowComponent : FlowComponent {
  @ForScope(MainScope::class)
  override fun flowCoroutineScope(): CoroutineScope

  @Provides
  @SingleIn(MainScope::class)
  @ForScope(MainScope::class)
  fun provideCoroutineScope(): CoroutineScope {
    return createCoroutineScope()
  }

  @ContributesTo(AppFlowScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): MainFlowComponent
  }
}
