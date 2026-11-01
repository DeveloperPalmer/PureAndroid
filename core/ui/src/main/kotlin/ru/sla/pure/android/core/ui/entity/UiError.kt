package ru.sla.pure.android.core.ui.entity

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class UiError(
  @param:StringRes
  val messageResource: Int,
  val cause: Throwable? = null,
)
