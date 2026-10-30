package ru.sla.pure.android.app.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.sla.pure.android.core.data.network.ServerJson
import ru.sla.pure.android.core.data.network.adapter.ApiErrorCallAdapterFactory
import java.util.concurrent.TimeUnit

@ContributesTo(AppScope::class)
interface NetworkModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .callTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl("https://rickandmortyapi.com/api/")
      .client(client)
      .addCallAdapterFactory(ApiErrorCallAdapterFactory())
      .addConverterFactory(ServerJson.asConverterFactory("application/json".toMediaType()))
      .build()
  }
}
