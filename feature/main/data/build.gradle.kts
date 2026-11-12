plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.data"
}

dependencies {
  implementation(projects.authSession.domain)
  implementation(projects.core.data)
  implementation(projects.feature.main.domain)
  implementation(projects.lib.google.authenticator)
}
