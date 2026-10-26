package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.DefineComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import ru.sla.pure.android.feature.cities.domain.di.CitiesScope

@CitiesScope
@DefineComponent(parent = ActivityRetainedComponent::class)
interface CitiesFlowComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): CitiesFlowComponent
    }
}
