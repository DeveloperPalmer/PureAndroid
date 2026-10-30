package ru.sla.pure.android.feature.main.domain.entity

data class CharacterPage(
  val characters: List<Character>,
  val page: Int,
  val totalCount: Int,
  val totalPages: Int,
  val nextPage: Int?,
  val previousPage: Int?,
)
