package ru.sla.pure.android.database.converter

import androidx.room.TypeConverter

internal class CharacterIdListConverter {
  @TypeConverter
  fun encode(ids: List<Int>): String {
    return ids.joinToString(",")
  }

  @TypeConverter
  fun decode(value: String): List<Int> {
    return if (value.isEmpty()) emptyList() else value.split(',').map(String::toInt)
  }
}
