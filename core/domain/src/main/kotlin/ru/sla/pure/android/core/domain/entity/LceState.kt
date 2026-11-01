package ru.sla.pure.android.core.domain.entity

sealed interface LceState<out T> {
  data object Loading : LceState<Nothing>
  data class Content<T>(val value: T) : LceState<T>
  data class Error(val value: Throwable) : LceState<Nothing>
}
