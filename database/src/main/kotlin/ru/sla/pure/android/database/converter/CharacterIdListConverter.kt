package ru.sla.pure.android.database.converter

import androidx.room3.ColumnTypeConverter

internal class CharacterIdListConverter {
  @ColumnTypeConverter
  fun encode(ids: List<Int>): String {
    return ids.joinToString(",")
  }

  @ColumnTypeConverter
  fun decode(value: String): List<Int> {
    return if (value.isEmpty()) emptyList() else value.split(',').map(String::toInt)
  }
}
