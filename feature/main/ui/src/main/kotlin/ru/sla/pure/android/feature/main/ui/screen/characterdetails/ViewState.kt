package ru.sla.pure.android.feature.main.ui.screen.characterdetails

import androidx.compose.runtime.Immutable
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.feature.main.domain.entity.Character

@Immutable
data class ViewState(
  val contentLoadState: ContentLoadState = ContentLoadState.Loading,
  val character: Character? = null
)
