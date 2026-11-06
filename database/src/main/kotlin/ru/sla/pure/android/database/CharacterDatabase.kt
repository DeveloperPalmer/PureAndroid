package ru.sla.pure.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity

@Database(
  entities = [CharacterEntity::class, CharacterPageEntity::class],
  version = 2,
  exportSchema = true,
)
abstract class CharacterDatabase : RoomDatabase() {
  abstract fun characterDao(): CharacterDao

  companion object {
    private val migration1To2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE character_pages ADD COLUMN characterIds TEXT NOT NULL DEFAULT ''")
        db.execSQL(
          """
          UPDATE character_pages
          SET characterIds = COALESCE((
            SELECT GROUP_CONCAT(characterId)
            FROM (
              SELECT characterId
              FROM character_page_entries
              WHERE character_page_entries.page = character_pages.page
              ORDER BY position
            )
          ), '')
          """.trimIndent(),
        )
        db.execSQL("DROP TABLE character_page_entries")
      }
    }

    fun create(context: Context): CharacterDatabase = Room.databaseBuilder(
      context.applicationContext,
      CharacterDatabase::class.java,
      "characters.db",
    ).addMigrations(migration1To2).build()
  }
}
