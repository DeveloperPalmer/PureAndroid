package ru.sla.pure.android.core.routing

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.cancel

/** Привязывает граф к хранилищу корня флоу и контролирует его освобождение. */
open class FlowComponentViewModel<C : FlowComponent>(
  val component: C,
) : ViewModel() {
  val viewModelFactory = component.metroViewModelFactory
  private val flow = component.flow()
  private val flowName = flow.javaClass.simpleName.ifEmpty { flow.javaClass.name }

  init {
    flow.onEntry()
    Log.d(LOG_TAG, "Starting [$flowName] flow")
  }

  override fun onCleared() {
    Log.d(LOG_TAG, "Finishing [$flowName] flow")
    component.coroutineScope().cancel()
    watchClearedFlow(this, component, component::class.java.name)
  }

  private companion object {
    const val LOG_TAG = "FlowLifecycle"
  }
}
