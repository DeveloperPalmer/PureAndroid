package ru.sla.pure.android.core.ui

import ru.sla.pure.android.core.domain.entity.LceState
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.error.ErrorMapper
import ru.sla.pure.android.core.ui.error.baseErrorMapper

fun LceState<*>.toUiLceState(errorMapper: ErrorMapper = ::baseErrorMapper): ContentLoadState {
  return when (this) {
    is LceState.Loading -> ContentLoadState.Loading
    is LceState.Content -> ContentLoadState.Ready
    is LceState.Error -> ContentLoadState.Error(error = errorMapper(value))
  }
}
