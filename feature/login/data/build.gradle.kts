plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.login.data"
}

dependencies {
  api(projects.feature.login.domain)
  api(projects.lib.google.authenticator)
  implementation(projects.authSession.domain)
  implementation(projects.core.data)
  implementation(projects.core.routing)
  implementation(libs.kotlinx.coroutines.play.services)
}
