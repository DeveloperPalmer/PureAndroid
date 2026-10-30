package ru.sla.pure.android.database.entity

data class CachedCharacterPage(
    val info: CharacterPageEntity,
    val characters: List<CharacterEntity>,
)
