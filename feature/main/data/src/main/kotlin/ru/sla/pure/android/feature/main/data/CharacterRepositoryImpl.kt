package ru.sla.pure.android.feature.main.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.openapi.schema.api.CharacterApi
import ru.sla.pure.android.feature.main.data.network.mapper.toDomainModel
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class CharacterRepositoryImpl(private val api: CharacterApi) : CharacterRepository {
    override suspend fun readPage(page: Int): CharacterPage {
        require(page > 0) { "Номер страницы должен быть положительным" }
        return api.readPage(page).toDomainModel(page)
    }

    override suspend fun readCharacter(id: Int): Character {
        require(id > 0) { "ID персонажа должен быть положительным" }
        return api.readCharacter(id).toDomainModel()
    }
}
