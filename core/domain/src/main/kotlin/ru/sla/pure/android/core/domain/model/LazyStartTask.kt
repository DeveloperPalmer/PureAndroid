package ru.sla.pure.android.core.domain.model

import kotlinx.coroutines.Job
import ru.kode.remo.QueueingStrategy
import ru.kode.remo.StartScheduled
import ru.kode.remo.Task0
import ru.kode.remo.Task1
import ru.kode.remo.Task2
import ru.kode.remo.Task3

fun Task0<*>.startOnSubscribe(
  queueingStrategy: QueueingStrategy = QueueingStrategy.SkipNew
): Job {
  return start(
    scheduled = StartScheduled.Lazily(),
    queueingStrategy = queueingStrategy
  )
}

fun <P1, R> Task1<P1, R>.startOnSubscribe(
  argument: P1,
  queueingStrategy: QueueingStrategy = QueueingStrategy.SkipNew
): Job {
  return start(
    argument = argument,
    scheduled = StartScheduled.Lazily(),
    queueingStrategy = queueingStrategy
  )
}

fun <P1, P2, R> Task2<P1, P2, R>.startOnSubscribe(
  argument1: P1,
  argument2: P2,
  queueingStrategy: QueueingStrategy = QueueingStrategy.SkipNew
): Job {
  return start(
    argument1 = argument1,
    argument2 = argument2,
    scheduled = StartScheduled.Lazily(),
    queueingStrategy = queueingStrategy
  )
}

fun <P1, P2, P3, R> Task3<P1, P2, P3, R>.startOnSubscribe(
  argument1: P1,
  argument2: P2,
  argument3: P3,
  queueingStrategy: QueueingStrategy = QueueingStrategy.SkipNew
): Job {
  return start(
    argument1 = argument1,
    argument2 = argument2,
    argument3 = argument3,
    scheduled = StartScheduled.Lazily(),
    queueingStrategy = queueingStrategy
  )
}
