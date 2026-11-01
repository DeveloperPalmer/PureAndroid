package ru.sla.pure.android.core.ui.error

import ru.sla.pure.android.core.ui.entity.UiError

typealias ErrorMapper = (Throwable) -> UiError

fun composeErrorMapper(
  vararg errorMappers: (Throwable) -> UiError?,
  defaultMapper: ErrorMapper = ::baseErrorMapper,
): ErrorMapper {
  return { error -> errorMappers.firstNotNullOfOrNull { mapper -> mapper(error) } ?: defaultMapper(error) }
}
