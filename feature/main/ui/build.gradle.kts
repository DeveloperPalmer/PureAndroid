plugins {
  id("android-library-convention")
  id("android-library-test-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.ui"
}

dependencies {
  api(projects.uikit)
  implementation(libs.paging.compose)
  implementation(projects.feature.main.domain)
}
