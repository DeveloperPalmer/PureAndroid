package ru.sla.pure.android

import android.app.Application
import dev.zacsweers.metro.createGraph
import ru.sla.pure.android.di.AppGraph

/**
 * Точка входа DI: держит [appGraph] — корневой граф Metro. Он создаётся при первом обращении
 * и живёт до конца процесса
 */
class PureApplication : Application() {

    val appGraph: AppGraph by lazy { createGraph<AppGraph>() }
}
