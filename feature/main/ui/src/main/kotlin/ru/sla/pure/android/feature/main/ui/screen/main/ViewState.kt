package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.entity.Character

data class ViewState(
  val characters: Flow<PagingData<Character>> = emptyFlow(),
  val contentLoadState: ContentLoadState = ContentLoadState.Loading,
  val refreshing: Boolean = false,
  val hasContent: Boolean = false,
) {
  /** Индикатор потягивания показываем только поверх существующего контента: при первичной
   *  загрузке вместо него полноэкранный спиннер, иначе индикаторов было бы два. */
  val refreshInProgress: Boolean get() = refreshing && hasContent
}
