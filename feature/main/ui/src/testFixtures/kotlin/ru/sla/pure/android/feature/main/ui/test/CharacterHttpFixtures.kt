package ru.sla.pure.android.feature.main.ui.test

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation

/** Network contracts owned by the feature, independent of generated transport models. */
object CharacterHttpFixtures {
  fun page(characters: List<Character>): String = buildJsonObject {
    put(
      "info",
      buildJsonObject {
        put("count", characters.size)
        put("pages", 1)
        put("next", JsonNull)
        put("prev", JsonNull)
      },
    )
    put(
      "results",
      buildJsonArray {
        characters.forEach { add(characterJson(it)) }
      },
    )
  }.toString()

  fun details(character: Character): String = characterJson(character).toString()

  private fun characterJson(character: Character): JsonObject = buildJsonObject {
    put("id", character.id)
    put("name", character.name)
    put("status", character.status.name)
    put("species", character.species)
    put("type", character.type)
    put("gender", character.gender.name)
    put("origin", locationJson(character.origin))
    put("location", locationJson(character.location))
    put("image", character.imageUrl)
  }

  private fun locationJson(location: CharacterLocation): JsonObject = buildJsonObject {
    put("name", location.name)
    put("url", location.url)
  }
}
