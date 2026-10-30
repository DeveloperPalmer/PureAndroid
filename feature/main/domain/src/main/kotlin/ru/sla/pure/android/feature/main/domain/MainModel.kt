package ru.sla.pure.android.feature.main.domain

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.di.MainScope

@SingleIn(MainScope::class)
@Inject
class MainModel(private val repository: CharacterRepository) {
    suspend fun readPage(page: Int): CharacterPage = repository.readPage(page)
}
