package ru.sla.pure.android.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import ru.sla.pure.android.BuildConfig
import ru.sla.pure.android.R
import ru.sla.pure.android.buildconfig.BuildConfigProvider
import ru.sla.pure.android.buildconfig.BuildType

@ContributesTo(AppScope::class)
interface BuildConfigurationModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideBuildConfigProvider(applicationContext: Context): BuildConfigProvider {
    return object : BuildConfigProvider {
      override val buildType: BuildType = when (BuildConfig.BUILD_TYPE) {
        "debug" -> BuildType.Debug
        "release" -> BuildType.Release
        else -> error("Unsupported build type: ${BuildConfig.BUILD_TYPE}")
      }

      override val googleWebClientId: String = applicationContext.getString(R.string.default_web_client_id)
    }
  }
}
