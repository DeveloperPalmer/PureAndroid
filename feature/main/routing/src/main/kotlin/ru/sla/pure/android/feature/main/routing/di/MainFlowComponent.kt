package ru.sla.pure.android.feature.main.routing.di

import dagger.hilt.DefineComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import ru.sla.pure.android.feature.main.domain.di.MainScope

@MainScope
@DefineComponent(parent = ActivityRetainedComponent::class)
interface MainFlowComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): MainFlowComponent
    }
}
