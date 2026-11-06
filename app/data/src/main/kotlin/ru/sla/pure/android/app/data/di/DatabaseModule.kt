package ru.sla.pure.android.app.data.di

import android.content.Context
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
  fun provideDatabase(applicationContext: Context): CharacterDatabase = CharacterDatabase.create(applicationContext)

  @Provides
  @SingleIn(AppScope::class)
  fun provideCharacterDao(database: CharacterDatabase): CharacterDao = database.characterDao()
}
