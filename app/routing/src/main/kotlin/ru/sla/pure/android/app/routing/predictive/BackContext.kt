package ru.sla.pure.android.app.routing.predictive

/** Неизменяемый снимок живых стеков для проверки непосредственно перед pop. */
internal data class BackStackSnapshot(
  val activeTab: Any,
  val entries: List<Any>,
)

internal data class BackContext(
  val stack: BackStackSnapshot,
  val outgoingKey: Any,
  val incomingKey: Any?,
  val isTabBack: Boolean,
)
