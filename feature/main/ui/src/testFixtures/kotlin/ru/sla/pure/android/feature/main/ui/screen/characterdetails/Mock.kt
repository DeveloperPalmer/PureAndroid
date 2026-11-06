package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

// Distinct values catch accidentally rendering one field in place of another.
val testCharacter = Character(
  id = 47,
  name = "Birdperson",
  status = CharacterStatus.Alive,
  species = "Bird-Person",
  type = "",
  gender = CharacterGender.Male,
  origin = CharacterLocation(name = "Bird World", url = ""),
  location = CharacterLocation(name = "Planet Squanch", url = ""),
  imageUrl = "",
)

internal fun testCharacter(id: Int = 47) = Character(
  id = id,
  name = "Character $id",
  status = CharacterStatus.Alive,
  species = "Human",
  type = "",
  gender = CharacterGender.Unknown,
  origin = CharacterLocation("Origin $id", ""),
  location = CharacterLocation("Location $id", ""),
  imageUrl = "",
)
