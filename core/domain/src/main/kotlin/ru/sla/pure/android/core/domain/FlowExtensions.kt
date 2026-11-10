package ru.sla.pure.android.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

fun <T, S : Any> Flow<T>.mapDistinctNotNullChanges(transform: suspend (T) -> S?): Flow<S> {
  return this.mapNotNull(transform).distinctUntilChanged()
}
