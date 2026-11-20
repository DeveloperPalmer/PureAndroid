package ru.sla.pure.android.core.routing

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.cancel

/** Привязывает граф к хранилищу корня флоу и контролирует его освобождение. */
class FlowComponentViewModel<C : FlowComponent>(
  val component: C,
) : ViewModel() {
  private val flow = component.flow()
  private val flowName = flow.javaClass.simpleName.ifEmpty { flow.javaClass.name }

  val entryRoutes = flow.onEntry()
  val viewModelFactory = component.metroViewModelFactory

  init {
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
