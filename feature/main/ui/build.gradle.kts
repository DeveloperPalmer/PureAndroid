plugins {
  id("compose-convention")
  id("metro-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.ui"
  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

dependencies {
  androidTestImplementation(libs.bundles.testing.compose)
  testImplementation(libs.bundles.testing.unit)
  api(projects.core.ui)
  implementation(projects.feature.main.domain)
  implementation(projects.uikit)
  implementation(libs.metrox.viewmodel)
  implementation(libs.paging.compose)

  androidTestImplementation(platform(libs.androidx.compose.bom))
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
