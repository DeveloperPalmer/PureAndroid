package ru.sla.pure.android.feature.cities.routing.di

import dagger.hilt.EntryPoints
import ru.sla.pure.android.core.routing.FlowGraph
import javax.inject.Inject
import javax.inject.Provider

/** Граф флоу «Города»: собирает [CitiesFlowComponent] и отдаёт записям его экраны */
class CitiesFlowGraph @Inject constructor(
    builder: Provider<CitiesFlowComponent.Builder>,
) : FlowGraph<CitiesScreens>({
    EntryPoints.get(builder.get().build(), CitiesScreens::class.java)
})
