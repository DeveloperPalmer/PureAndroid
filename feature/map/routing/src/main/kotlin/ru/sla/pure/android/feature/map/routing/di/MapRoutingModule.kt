package ru.sla.pure.android.feature.map.routing.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.feature.map.routing.MapEntryProviderInstaller

/**
 * Роутинг фичи «Карта» в общих наборах приложения. Экран карты событий пока не шлёт,
 * поэтому навигации (`FlowNode`) у фичи нет
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
interface MapRoutingModule {

    @Binds
    @IntoSet
    fun bindMapEntryProviderInstaller(installer: MapEntryProviderInstaller): EntryProviderInstaller
}
