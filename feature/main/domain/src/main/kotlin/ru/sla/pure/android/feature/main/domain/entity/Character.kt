package ru.sla.pure.android.feature.main.domain.entity

data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val origin: CharacterLocation,
    val location: CharacterLocation,
    val imageUrl: String,
)

data class CharacterLocation(val name: String, val url: String)

enum class CharacterStatus { Alive, Dead, Unknown }
enum class CharacterGender { Female, Male, Genderless, Unknown }
