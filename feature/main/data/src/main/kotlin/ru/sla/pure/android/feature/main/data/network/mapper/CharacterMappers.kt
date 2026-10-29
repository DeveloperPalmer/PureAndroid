package ru.sla.pure.android.feature.main.data.network.mapper

import ru.sla.pure.android.openapi.schema.model.CharacterNM
import ru.sla.pure.android.openapi.schema.model.CharacterPageNM
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

internal fun CharacterNM.toDomainModel() = Character(
    id = id,
    name = name,
    status = when (status) {
        "Alive" -> CharacterStatus.Alive
        "Dead" -> CharacterStatus.Dead
        else -> CharacterStatus.Unknown
    },
    species = species,
    type = type,
    gender = when (gender) {
        "Female" -> CharacterGender.Female
        "Male" -> CharacterGender.Male
        "Genderless" -> CharacterGender.Genderless
        else -> CharacterGender.Unknown
    },
    origin = CharacterLocation(origin.name, origin.url),
    location = CharacterLocation(location.name, location.url),
    imageUrl = image,
)

internal fun CharacterPageNM.toDomainModel(page: Int) = CharacterPage(
    characters = results.map { it.toDomainModel() },
    page = page,
    totalCount = info.count,
    totalPages = info.pages,
    nextPage = if (info.next != null) page + 1 else null,
    previousPage = if (info.prev != null) page - 1 else null,
)
