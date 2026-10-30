plugins {
  id("android-library-convention")
  id("android-library-test-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
  api(projects.core.domain)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.navigation3.runtime)
  api(libs.androidx.navigation3.ui)
  api(libs.bundles.viewmodel)
}
