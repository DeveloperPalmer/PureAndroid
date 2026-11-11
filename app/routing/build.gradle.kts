plugins {
  id("android-library-convention")
  id("android-test-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.app.routing"
}

dependencies {
  api(projects.core.routing)
  implementation(projects.uikit)
}
