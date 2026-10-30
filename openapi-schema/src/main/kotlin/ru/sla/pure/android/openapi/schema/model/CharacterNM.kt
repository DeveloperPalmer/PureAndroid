package ru.sla.pure.android.openapi.schema.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterNM(
  val id: Int,
  val name: String,
  val status: String,
  val species: String,
  val type: String,
  val gender: String,
  val origin: CharacterLocationNM,
  val location: CharacterLocationNM,
  val image: String,
)

@Serializable
data class CharacterLocationNM(val name: String, val url: String)

@Serializable
data class CharacterPageNM(val info: PageInfoNM, val results: List<CharacterNM>)

@Serializable
data class PageInfoNM(val count: Int, val pages: Int, val next: String?, val prev: String?)
