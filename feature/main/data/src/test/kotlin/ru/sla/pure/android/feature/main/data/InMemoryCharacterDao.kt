package ru.sla.pure.android.feature.main.data

import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.database.entity.CachedCharacterPage
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity
import ru.sla.pure.android.database.entity.CharacterPageEntryEntity

internal class InMemoryCharacterDao : CharacterDao() {
  private val characters = mutableMapOf<Int, CharacterEntity>()
  private val pages = mutableMapOf<Int, Pair<CharacterPageEntity, List<Int>>>()
  var writeFailure: Exception? = null

  override suspend fun readCharacter(id: Int) = characters[id]

  override suspend fun saveCharacter(character: CharacterEntity) {
    writeFailure?.let { throw it }
    characters[character.id] = character
  }

  override suspend fun readPage(page: Int): CachedCharacterPage? {
    val (info, ids) = pages[page] ?: return null
    return CachedCharacterPage(info, ids.map { characters.getValue(it) })
  }

  override suspend fun savePage(info: CharacterPageEntity, characters: List<CharacterEntity>) {
    writeFailure?.let { throw it }
    characters.forEach { this.characters[it.id] = it }
    pages[info.page] = info to characters.map { it.id }
  }

  override suspend fun readPageInfo(page: Int): CharacterPageEntity? = error("Unused by fake")
  override suspend fun readPageCharacters(page: Int): List<CharacterEntity> = error("Unused by fake")
  override suspend fun saveCharacters(characters: List<CharacterEntity>): Unit = error("Unused by fake")
  override suspend fun savePageInfo(info: CharacterPageEntity): Unit = error("Unused by fake")
  override suspend fun deletePageEntries(page: Int): Unit = error("Unused by fake")
  override suspend fun savePageEntries(entries: List<CharacterPageEntryEntity>): Unit = error("Unused by fake")
}
