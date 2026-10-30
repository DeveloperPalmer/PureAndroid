package ru.sla.pure.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import ru.sla.pure.android.database.entity.CachedCharacterPage
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity
import ru.sla.pure.android.database.entity.CharacterPageEntryEntity

@Dao
abstract class CharacterDao {
  @Query("SELECT * FROM characters WHERE id = :id")
  abstract suspend fun readCharacter(id: Int): CharacterEntity?

  @Upsert
  abstract suspend fun saveCharacter(character: CharacterEntity)

  @Transaction
  open suspend fun readPage(page: Int): CachedCharacterPage? {
    val info = readPageInfo(page) ?: return null
    return CachedCharacterPage(info, readPageCharacters(page))
  }

  @Transaction
  open suspend fun savePage(info: CharacterPageEntity, characters: List<CharacterEntity>) {
    require(info.page > 0)
    require(characters.map { it.id }.distinct().size == characters.size)
    saveCharacters(characters)
    savePageInfo(info)
    deletePageEntries(info.page)
    savePageEntries(
      characters.mapIndexed { index, character ->
        CharacterPageEntryEntity(info.page, character.id, index)
      }
    )
  }

  @Query("SELECT * FROM character_pages WHERE page = :page")
  protected abstract suspend fun readPageInfo(page: Int): CharacterPageEntity?

  @Query(
    """
        SELECT characters.* FROM characters
        INNER JOIN character_page_entries ON characters.id = character_page_entries.characterId
        WHERE character_page_entries.page = :page
        ORDER BY character_page_entries.position
    """
  )
  protected abstract suspend fun readPageCharacters(page: Int): List<CharacterEntity>

  @Upsert
  protected abstract suspend fun saveCharacters(characters: List<CharacterEntity>)

  @Upsert
  protected abstract suspend fun savePageInfo(info: CharacterPageEntity)

  @Query("DELETE FROM character_page_entries WHERE page = :page")
  protected abstract suspend fun deletePageEntries(page: Int)

  @Insert
  protected abstract suspend fun savePageEntries(entries: List<CharacterPageEntryEntity>)
}
