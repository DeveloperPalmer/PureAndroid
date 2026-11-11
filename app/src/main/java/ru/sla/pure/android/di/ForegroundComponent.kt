package ru.sla.pure.android.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import ru.sla.pure.android.core.domain.di.ForegroundScope
import ru.sla.pure.android.feature.app.di.AppFlowComponent

@GraphExtension(ForegroundScope::class)
interface ForegroundComponent {
  fun appFlowFactory(): AppFlowComponent.Factory

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(): ForegroundComponent
  }
}
