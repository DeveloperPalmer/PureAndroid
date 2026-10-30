package ru.sla.pure.android.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Scope для моделей скоупа (флоу, приложения). Его владелец — DI-компонент скоупа, а отменяет его
 * тот, кто держит компонент, когда скоуп закрывается. `SupervisorJob`: падение одной корутины
 * не отменяет остальные
 */
fun createCoroutineScope(): CoroutineScope {
  return CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
