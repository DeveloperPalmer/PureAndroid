package ru.sla.pure.android.feature.login.routing.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.CoroutineScope
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.core.routing.Flow
import ru.sla.pure.android.core.routing.FlowComponent
import ru.sla.pure.android.core.routing.FlowViewModelFactory
import ru.sla.pure.android.core.routing.Navigator
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.feature.login.domain.di.LoginScope

@GraphExtension(LoginScope::class, bindingContainers = [LoginFlowModule::class])
interface LoginFlowComponent : FlowComponent {
  @ContributesTo(AppFlowScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createLoginFlowComponent(
      @Provides @ForScope(LoginScope::class) navigator: Navigator,
    ): LoginFlowComponent
  }

  @ForScope(LoginScope::class)
  override fun flow(): Flow

  @ForScope(LoginScope::class)
  override fun coroutineScope(): CoroutineScope
}

@BindingContainer
interface LoginFlowModule {
  @Binds
  val FlowViewModelFactory.bindMetroViewModelFactory: MetroViewModelFactory

  companion object {
    @Provides
    @SingleIn(LoginScope::class)
    @ForScope(LoginScope::class)
    fun provideCoroutineScope(): CoroutineScope = createCoroutineScope()
  }
}
