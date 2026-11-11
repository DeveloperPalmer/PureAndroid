plugins {
  id("android-application-convention")
  id("android-test-convention")
  alias(libs.plugins.google.services)
}

android {
  namespace = "ru.sla.pure.android"

  buildFeatures {
    buildConfig = true
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
      signingConfig = signingConfigs.getByName("debug")
      isDebuggable = true
    }
    release {
      isDebuggable = false
    }
  }

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
  constraints {
    debugImplementation(libs.androidx.drawerlayout) {
      because("Для ресурсов Material в Android-тестах нужен атрибут drawerLayoutStyle")
    }
  }

  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.core.splashscreen)
  implementation(projects.app.data)
  implementation(projects.app.routing)
  implementation(projects.buildConfig)
  implementation(projects.feature.app.routing)

  debugImplementation(libs.androidx.concurrent.futures)
  debugImplementation(libs.androidx.espresso.idling.resource)

  androidTestImplementation(libs.androidx.concurrent.futures)
  androidTestImplementation(libs.androidx.espresso.idling.resource)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(projects.app.testing)
  androidTestImplementation(testFixtures(projects.feature.main.ui))
  androidTestUtil(libs.androidx.test.orchestrator)
}
