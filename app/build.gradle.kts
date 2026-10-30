plugins {
  id("android-application-convention")
}

android {
  namespace = "ru.sla.pure.android"

  defaultConfig {
    applicationId = "ru.sla.pure.android"
    versionCode = 1
    versionName = "1.0"
  }
}

dependencies {
  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.annotation)
  implementation(projects.app.data)
  implementation(projects.core.routing)
  implementation(projects.feature.main.routing)
  implementation(projects.feature.map.routing)

  testImplementation(libs.bundles.testing.unit)

  androidTestImplementation(libs.bundles.testing.android)
  androidTestRuntimeOnly(libs.androidx.test.runner)
}
