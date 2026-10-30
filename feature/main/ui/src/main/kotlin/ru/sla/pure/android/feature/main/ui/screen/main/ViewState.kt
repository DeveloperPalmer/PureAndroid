package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.sla.pure.android.feature.main.domain.entity.Character

data class ViewState(
  val characters: Flow<PagingData<Character>> = emptyFlow(),
)
