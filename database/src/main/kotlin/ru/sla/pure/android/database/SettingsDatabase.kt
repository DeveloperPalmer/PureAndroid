package ru.sla.pure.android.database

import android.content.Context
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import ru.sla.pure.android.database.converter.DistinctFlowConverter
import ru.sla.pure.android.database.dao.SettingsDao
import ru.sla.pure.android.database.entity.SettingsEntity

@Database(
  version = 1,
  exportSchema = true,
  entities = [SettingsEntity::class]
)
@DaoReturnTypeConverters(
  DistinctFlowConverter::class
)
abstract class SettingsDatabase : RoomDatabase() {
  abstract fun settingsDao(): SettingsDao

  companion object {
    fun create(context: Context): SettingsDatabase {
      return Room.databaseBuilder(context.applicationContext, SettingsDatabase::class.java, "settings.db")
        .setDriver(AndroidSQLiteDriver())
        .build()
    }
  }
}
