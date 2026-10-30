package ru.sla.pure.android.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
  tableName = "character_page_entries",
  primaryKeys = ["page", "characterId"],
  indices = [Index("characterId"), Index(value = ["page", "position"], unique = true)],
  foreignKeys = [
    ForeignKey(
      entity = CharacterPageEntity::class,
      parentColumns = ["page"],
      childColumns = ["page"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = CharacterEntity::class,
      parentColumns = ["id"],
      childColumns = ["characterId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class CharacterPageEntryEntity(val page: Int, val characterId: Int, val position: Int)
