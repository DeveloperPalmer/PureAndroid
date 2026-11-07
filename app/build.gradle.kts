plugins {
  id("android-application-convention")
  id("android-test-convention")
}

android {
  namespace = "ru.sla.pure.android"

  testOptions {
    animationsDisabled = true
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }

  defaultConfig {
    applicationId = "ru.sla.pure.android"
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "ru.sla.pure.android.testing.core.TestAppRunner"
    testInstrumentationRunnerArguments["clearPackageData"] = "true"
  }
}

dependencies {
  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.annotation)
  implementation(projects.app.data)
  implementation(projects.feature.app)

  debugImplementation(libs.androidx.concurrent.futures)
  debugImplementation(libs.androidx.espresso.idling.resource)

  androidTestImplementation(libs.androidx.concurrent.futures)
  androidTestImplementation(libs.androidx.espresso.idling.resource)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(projects.app.testing)
  androidTestImplementation(testFixtures(projects.feature.main.ui))
  androidTestUtil(libs.androidx.test.orchestrator)
}
