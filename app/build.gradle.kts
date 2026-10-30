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
  androidTestImplementation(libs.bundles.testing.compose)
  testImplementation(libs.bundles.testing.unit)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  androidTestImplementation(platform(libs.androidx.compose.bom))
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  implementation(projects.uikit)
  implementation(projects.core.routing)
  implementation(projects.app.data)
  implementation(projects.feature.main.routing)
  implementation(projects.feature.map.routing)
}
