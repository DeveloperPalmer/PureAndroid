plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.login.routing"
}

dependencies {
  api(projects.feature.login.data)
  api(projects.feature.login.ui)
  implementation(projects.buildConfig)
  implementation(projects.core.routing)
}
