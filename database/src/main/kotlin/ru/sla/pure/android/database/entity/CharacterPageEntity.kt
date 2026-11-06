package ru.sla.pure.android.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.sla.pure.android.database.converter.CharacterIdListConverter

@Entity(tableName = "character_pages")
@TypeConverters(CharacterIdListConverter::class)
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
