package ru.sla.pure.android.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity

@Dao
abstract class CharacterDao {
  @Query("SELECT * FROM characters WHERE id = :id")
  abstract suspend fun readCharacter(id: Int): CharacterEntity?

  @Upsert
  abstract suspend fun saveCharacter(character: CharacterEntity)

  @Transaction
  open suspend fun readPage(page: Int): Pair<CharacterPageEntity, List<CharacterEntity>>? {
    val characterPage = readPageInfo(page) ?: return null
    val charactersById = readCharacters(characterPage.characterIds).associateBy { it.id }
    return characterPage to characterPage.characterIds.mapNotNull(charactersById::get)
  }

  @Transaction
  open suspend fun savePage(info: CharacterPageEntity, characters: List<CharacterEntity>) {
    require(info.current > 0)

    val characterIds = characters.map { it.id }
    require(characterIds.distinct().size == characters.size)

    saveCharacters(characters)
    savePageInfo(info.copy(characterIds = characterIds))
  }

  @Query("SELECT * FROM character_pages WHERE page = :page")
  protected abstract suspend fun readPageInfo(page: Int): CharacterPageEntity?

  @Query("SELECT * FROM characters WHERE id IN (:ids)")
  protected abstract suspend fun readCharacters(ids: List<Int>): List<CharacterEntity>

  @Upsert
  protected abstract suspend fun saveCharacters(characters: List<CharacterEntity>)

  @Upsert
  protected abstract suspend fun savePageInfo(info: CharacterPageEntity)
}
