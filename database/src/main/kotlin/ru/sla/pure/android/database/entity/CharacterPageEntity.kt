package ru.sla.pure.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_pages")
data class CharacterPageEntity(
  @PrimaryKey val page: Int,
  val totalCount: Int,
  val totalPages: Int,
  val nextPage: Int?,
  val previousPage: Int?,
)
