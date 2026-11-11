package ru.sla.pure.android

import android.app.Application
import dev.zacsweers.metro.createGraphFactory
import ru.sla.pure.android.di.AppComponent
import ru.sla.pure.android.di.ForegroundComponent

/**
 * Точка входа DI: держит [appComponent] — корневой граф Metro. Он создаётся при первом обращении
 * и живёт до конца процесса
 */
open class PureApplication : Application() {
  val appComponent: AppComponent by lazy { createGraphFactory<AppComponent.Factory>().create(this) }
  open val foregroundFactory: ForegroundComponent.Factory
    get() = appComponent.foregroundFactory()
}
