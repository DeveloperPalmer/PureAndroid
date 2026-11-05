package ru.sla.pure.android.core.domain.entity

data class ApiError(
  val status: Int,
  val code: String?,
  val description: String?,
) : Throwable("server returned $status (code=${code ?: "none"}): ${description ?: "no message"}")
