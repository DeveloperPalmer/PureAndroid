plugins {
  id("android-library-convention")
  id("compose-convention")
}

android {
  namespace = "ru.sla.pure.android.core.ui"
}

dependencies {
  api(projects.core.domain)
  implementation(projects.core.resources)
  api(libs.bundles.compose)
  api(libs.androidx.lifecycle.common)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.metrox.viewmodel)
  api(libs.orbit.core)
  api(libs.orbit.viewmodel)
  api(libs.orbit.compose)
}
