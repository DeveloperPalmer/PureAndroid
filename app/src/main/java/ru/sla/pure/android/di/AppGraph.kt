package ru.sla.pure.android.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import ru.sla.pure.android.feature.app.di.AppFlowComponent

/**
 * Корневой граф: живёт столько же, сколько процесс. `@DependencyGraph(AppScope::class)` добавляет
 * в него всё, что модули вложили в `AppScope`: привязки, наборы и фабрики графов флоу. Поэтому
 * здесь перечислено только то, что нужно самому приложению
 */
@DependencyGraph(AppScope::class)
interface AppGraph {
  fun appFlowFactory(): AppFlowComponent.Factory

  @DependencyGraph.Factory
  interface Factory {
    fun create(
      @Provides applicationContext: Context
    ): AppGraph
  }
}
