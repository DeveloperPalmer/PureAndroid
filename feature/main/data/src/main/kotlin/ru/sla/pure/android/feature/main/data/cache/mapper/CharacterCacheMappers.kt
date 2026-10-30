package ru.sla.pure.android.feature.main.data.cache.mapper

import ru.sla.pure.android.database.entity.CachedCharacterPage
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

internal fun Character.toCacheEntity() = CharacterEntity(
  id = id,
  name = name,
  status = status.name,
  species = species,
  type = type,
  gender = gender.name,
  originName = origin.name,
  originUrl = origin.url,
  locationName = location.name,
  locationUrl = location.url,
  imageUrl = imageUrl,
)

internal fun CharacterEntity.toDomainModel() = Character(
  id = id,
  name = name,
  status = CharacterStatus.entries.firstOrNull { it.name == status } ?: CharacterStatus.Unknown,
  species = species,
  type = type,
  gender = CharacterGender.entries.firstOrNull { it.name == gender } ?: CharacterGender.Unknown,
  origin = CharacterLocation(originName, originUrl),
  location = CharacterLocation(locationName, locationUrl),
  imageUrl = imageUrl,
)

internal fun CharacterPage.toCachePageInfo() = CharacterPageEntity(
  page = page,
  totalCount = totalCount,
  totalPages = totalPages,
  nextPage = nextPage,
  previousPage = previousPage,
)

internal fun CachedCharacterPage.toDomainModel() = CharacterPage(
  characters = characters.map { it.toDomainModel() },
  page = info.page,
  totalCount = info.totalCount,
  totalPages = info.totalPages,
  nextPage = info.nextPage,
  previousPage = info.previousPage,
)
