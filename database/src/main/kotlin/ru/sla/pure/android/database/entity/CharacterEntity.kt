package ru.sla.pure.android.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
  @PrimaryKey
  val id: Int,
  val name: String,
  val status: String,
  val species: String,
  val type: String,
  val gender: String,
  val originName: String,
  val originUrl: String,
  val locationName: String,
  val locationUrl: String,
  val imageUrl: String,
)
