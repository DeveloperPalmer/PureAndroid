plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.lib.google.authenticator"
}

dependencies {
  api(libs.firebase.auth)
  implementation(projects.buildConfig)
  implementation(projects.core.domain)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services.auth)
  implementation(libs.google.id)
  implementation(libs.kotlin.result.coroutines)
  implementation(libs.kotlinx.coroutines.play.services)
}
