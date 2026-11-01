package ru.sla.pure.android.feature.main.domain.paging

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import ru.sla.pure.android.core.domain.entity.LceState

fun CombinedLoadStates.asLceState(): LceState<Unit> {
  val error = (refresh as? LoadState.Error)
    ?: (append as? LoadState.Error)
    ?: (prepend as? LoadState.Error)
  return when {
    refresh is LoadState.Loading || append is LoadState.Loading || prepend is LoadState.Loading -> {
      LceState.Loading
    }
    error != null -> {
      LceState.Error(value = error.error)
    }
    else -> {
      LceState.Content(value = Unit)
    }
  }
}
