package ru.sla.pure.android.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.ColumnTypeConverters
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ru.sla.pure.android.database.converter.CharacterIdListConverter

@Entity(tableName = "character_pages")
@ColumnTypeConverters(CharacterIdListConverter::class)
data class CharacterPageEntity(
  @PrimaryKey
  @ColumnInfo(name = "page")
  val current: Int,
  val totalCount: Int,
  val totalPages: Int,
  val nextPage: Int?,
  val previousPage: Int?,
  @ColumnInfo(defaultValue = "''")
  val characterIds: List<Int> = emptyList(),
)
