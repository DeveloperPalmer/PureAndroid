package ru.sla.pure.android.feature.main.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.auth.session.domain.AuthSessionRepository
import ru.sla.pure.android.core.domain.ReactiveModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.paging.CharacterPagingSource

@SingleIn(MainScope::class)
class MainModel @Inject constructor(
  @ForScope(MainScope::class)
  coroutineScope: CoroutineScope,
  private val mainRepository: MainRepository,
  private val authSessionRepository: AuthSessionRepository
) : ReactiveModel(coroutineScope) {
  val characters = Pager(
    config = PagingConfig(
      pageSize = PAGE_SIZE,
      initialLoadSize = PAGE_SIZE,
      enablePlaceholders = false
    ),
    pagingSourceFactory = { CharacterPagingSource(mainRepository) },
  ).flow.cachedIn(scope)

  val loadCharacter = task<Int, Character> { characterId ->
    mainRepository.readCharacter(characterId)
  }

  val logout = task<Unit> {
    mainRepository.logout()
    authSessionRepository.reset(cleanupStorage = true)
  }
}

internal const val PAGE_SIZE = 20
