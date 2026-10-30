package ru.sla.pure.android.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ReactiveModel(parentScope: CoroutineScope) : ru.kode.remo.ReactiveModel(parentScope) {
  init {
    uncaughtExceptions.onEach { it.printStackTrace() }.launchIn(scope)
  }
}
