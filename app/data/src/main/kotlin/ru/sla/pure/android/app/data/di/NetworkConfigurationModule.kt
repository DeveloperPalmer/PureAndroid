package ru.sla.pure.android.app.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface NetworkConfigurationModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideApiBaseUrl(): ApiBaseUrl = ApiBaseUrl("https://rickandmortyapi.com/api/")
}
