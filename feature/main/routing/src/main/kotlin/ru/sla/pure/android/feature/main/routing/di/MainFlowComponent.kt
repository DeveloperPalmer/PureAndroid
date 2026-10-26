package ru.sla.pure.android.feature.main.routing.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.feature.main.domain.di.MainScope
import ru.sla.pure.android.feature.main.ui.screen.main.MainViewModel

/**
 * Граф флоу «Главная» — расширение графа приложения: видит его привязки, а свои
 * `@SingleIn(MainScope::class)` держит в одном экземпляре на флоу. Metro генерирует его вместе
 * с графом приложения и добавляет в него всё, что вложено в `MainScope`
 */
@GraphExtension(MainScope::class)
interface MainFlowComponent : FlowComponent {

    fun mainViewModel(): MainViewModel

    @ForScope(MainScope::class)
    override fun flowCoroutineScope(): CoroutineScope

    @Provides
    @SingleIn(MainScope::class)
    @ForScope(MainScope::class)
    fun provideCoroutineScope(): CoroutineScope {
        return createCoroutineScope()
    }

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(): MainFlowComponent
    }
}
