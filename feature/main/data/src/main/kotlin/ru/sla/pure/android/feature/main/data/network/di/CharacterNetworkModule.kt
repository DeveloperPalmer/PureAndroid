package ru.sla.pure.android.feature.main.data.network.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import retrofit2.Retrofit
import ru.sla.pure.android.openapi.schema.api.CharacterApi

@ContributesTo(AppScope::class)
interface CharacterNetworkModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideCharacterApiClient(retrofit: Retrofit): CharacterApi = retrofit.create(CharacterApi::class.java)
}
