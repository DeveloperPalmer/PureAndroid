package ru.sla.pure.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity
import ru.sla.pure.android.database.entity.CharacterPageEntryEntity

@Database(
  entities = [CharacterEntity::class, CharacterPageEntity::class, CharacterPageEntryEntity::class],
  version = 1,
  exportSchema = true,
)
abstract class CharacterDatabase : RoomDatabase() {
  abstract fun characterDao(): CharacterDao

  companion object {
    fun create(context: Context): CharacterDatabase = Room.databaseBuilder(
      context.applicationContext,
      CharacterDatabase::class.java,
      "characters.db",
    ).build()
  }
}
