plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.login.ui"
}

dependencies {
  api(projects.feature.login.domain)
  implementation(libs.androidx.activity.compose)
  implementation(projects.authSession.domain)
  implementation(projects.uikit)
}
