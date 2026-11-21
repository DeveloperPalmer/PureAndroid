plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.lib.debugpanel.ui"
}

dependencies {
  api(projects.buildConfig)
  api(projects.uikit)
  implementation(libs.androidx.activity.compose)
  implementation(projects.lib.debugPanel.domain)
}
