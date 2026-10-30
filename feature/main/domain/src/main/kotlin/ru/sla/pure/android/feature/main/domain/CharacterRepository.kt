package ru.sla.pure.android.feature.main.domain

import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage

interface CharacterRepository {
  suspend fun readPage(page: Int = 1): CharacterPage
  suspend fun readCharacter(id: Int): Character
}
