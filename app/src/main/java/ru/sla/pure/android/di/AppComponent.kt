package ru.sla.pure.android.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.domain.di.ApplicationContext

@DependencyGraph(AppScope::class)
interface AppComponent {
  fun foregroundFactory(): ForegroundComponent.Factory

  @DependencyGraph.Factory
  interface Factory {
    fun create(
      @Provides
      @ApplicationContext
      context: Context
    ): AppComponent
  }
}

@ContributesTo(AppScope::class)
interface AppModule {
  @Provides
  @SingleIn(AppScope::class)
  @ForScope(AppScope::class)
  fun provideCoroutineScope(): CoroutineScope = createCoroutineScope()
}
