package ru.sla.pure.android.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity

@Database(
  version = 2,
  exportSchema = true,
  entities = [CharacterEntity::class, CharacterPageEntity::class]
)
abstract class CharacterDatabase : RoomDatabase() {
  abstract fun characterDao(): CharacterDao

  companion object {
    fun create(context: Context): CharacterDatabase {
      return Room.databaseBuilder(context.applicationContext, CharacterDatabase::class.java, "characters.db")
        .setDriver(AndroidSQLiteDriver())
        .build()
    }
  }
}
