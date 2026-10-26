package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.DefineComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import ru.sla.pure.android.feature.cities.domain.di.CitiesFlowScoped

/**
 * Граф флоу «Города». Даёт `@CitiesFlowScoped`-объекты по одному на флоу и видит всё из
 * `ActivityRetainedComponent`. Сам Hilt компонент не создаёт и не уничтожает: экземпляр собирается
 * через [Builder], а живёт столько, сколько живёт тот, кто его держит
 */
@CitiesFlowScoped
@DefineComponent(parent = ActivityRetainedComponent::class)
interface CitiesFlowComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): CitiesFlowComponent
    }
}
