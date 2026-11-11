package ru.sla.pure.android.feature.login.data.di

import android.app.Activity
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import ru.sla.pure.android.core.domain.di.ActivityContext
import ru.sla.pure.android.core.routing.di.AppFlowScope
import ru.sla.pure.android.feature.login.domain.LoginRepository
import ru.sla.pure.android.feature.login.domain.di.LoginScope

@GraphExtension(LoginScope::class)
interface LoginComponent {
  fun loginRepository(): LoginRepository

  @ContributesTo(AppFlowScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(
      @Provides
      @ActivityContext
      activity: Activity
    ): LoginComponent
  }
}
