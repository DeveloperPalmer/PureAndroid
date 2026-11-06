package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

// Distinct values catch accidentally rendering one field in place of another.
internal val detailsCharacter = Character(
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
