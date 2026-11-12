plugins {
  id("android-library-convention")
  id("compose-convention")
}

android {
  namespace = "ru.sla.pure.android.uikit"
}

dependencies {
  api(projects.core.ui)
  api(projects.core.resources)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
}
