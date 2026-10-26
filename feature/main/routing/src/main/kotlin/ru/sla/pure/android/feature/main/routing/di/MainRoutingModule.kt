package ru.sla.pure.android.feature.main.routing.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ru.sla.pure.android.core.routing.EntryProviderInstaller
import ru.sla.pure.android.feature.main.routing.MainEntryProviderInstaller

/**
 * Роутинг фичи «Главная» в общих наборах приложения. Главный экран событий пока не шлёт,
 * поэтому навигации (`FlowNode`) у фичи нет
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
interface MainRoutingModule {

    @Binds
    @IntoSet
    fun bindEntryProviderInstaller(installer: MainEntryProviderInstaller): EntryProviderInstaller
}
