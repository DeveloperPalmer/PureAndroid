plugins {
  id("android-library-convention")
  id("android-test-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.app.ui"
}

dependencies {
  api(projects.uikit)
  implementation(projects.core.routing)
  implementation(projects.authSession.domain)
}
