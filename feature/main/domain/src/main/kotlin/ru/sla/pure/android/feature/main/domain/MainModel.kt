package ru.sla.pure.android.feature.main.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.ReactiveModel
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.domain.paging.CharacterPagingSource

@SingleIn(MainScope::class)
@Inject
class MainModel(
  @ForScope(MainScope::class) coroutineScope: CoroutineScope,
  repository: CharacterRepository,
) : ReactiveModel(coroutineScope) {
  val characters = Pager(
    config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false),
    pagingSourceFactory = { CharacterPagingSource(repository) },
  ).flow.cachedIn(scope)
}
