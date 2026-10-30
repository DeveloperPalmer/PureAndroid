package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import org.orbitmvi.orbit.viewmodel.orbitContainer
import ru.sla.pure.android.core.ui.mvi.BaseViewModel
import ru.sla.pure.android.feature.main.domain.MainModel
import ru.sla.pure.android.feature.main.domain.di.MainScope

@ViewModelKey
@ContributesIntoMap(MainScope::class, binding<ViewModel>())
class MainViewModel @Inject constructor(
    private val mainModel: MainModel,
) : BaseViewModel<ViewState, Nothing>() {
    override val container = orbitContainer<ViewState, Nothing>(ViewState()) {
        reduce { state.copy(characters = mainModel.characters) }
    }
}
