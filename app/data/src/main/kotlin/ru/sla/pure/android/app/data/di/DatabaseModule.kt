package ru.sla.pure.android.app.data.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.database.CharacterDatabase
import ru.sla.pure.android.database.dao.CharacterDao

@ContributesTo(AppScope::class)
interface DatabaseModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabase(application: Application): CharacterDatabase = CharacterDatabase.create(application)

  @Provides
  @SingleIn(AppScope::class)
  fun provideCharacterDao(database: CharacterDatabase): CharacterDao = database.characterDao()
}
