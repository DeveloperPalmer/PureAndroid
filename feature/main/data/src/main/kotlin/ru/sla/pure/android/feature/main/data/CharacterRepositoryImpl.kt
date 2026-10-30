package ru.sla.pure.android.feature.main.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.feature.main.data.cache.mapper.toCacheEntity
import ru.sla.pure.android.feature.main.data.cache.mapper.toCachePageInfo
import ru.sla.pure.android.feature.main.data.cache.mapper.toDomainModel
import ru.sla.pure.android.feature.main.data.network.mapper.toDomainModel
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.openapi.schema.api.CharacterApi

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class CharacterRepositoryImpl(
  private val api: CharacterApi,
  private val dao: CharacterDao,
) : CharacterRepository {
  override suspend fun readPage(page: Int): CharacterPage {
    require(page > 0) { "Номер страницы должен быть положительным" }
    val response = try {
      api.readPage(page)
    } catch (error: ServerException.Unreachable) {
      return dao.readPage(page)?.toDomainModel() ?: throw error
    }
    val result = response.toDomainModel(page)
    dao.savePage(result.toCachePageInfo(), result.characters.map { it.toCacheEntity() })
    return result
  }

  override suspend fun readCharacter(id: Int): Character {
    require(id > 0) { "ID персонажа должен быть положительным" }
    val response = try {
      api.readCharacter(id)
    } catch (error: ServerException.Unreachable) {
      return dao.readCharacter(id)?.toDomainModel() ?: throw error
    }
    val result = response.toDomainModel()
    dao.saveCharacter(result.toCacheEntity())
    return result
  }
}
