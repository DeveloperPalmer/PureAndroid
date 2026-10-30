plugins {
  id("com.android.application")
  id("detekt-convention")
  id("org.jetbrains.kotlin.plugin.compose")
  id("metro-convention")
}

val libs = versionCatalogs.named("libs")

// Общие настройки Android-приложения: UI на Compose, DI на Metro.
// Модуль задаёт только своё: namespace, applicationId, версии
android {
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    minSdk = 26
    targetSdk = 37

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      optimization {
        enable = false
      }
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(platform(libs.findLibrary("androidx.compose.bom").get()))
  implementation(libs.findBundle("compose").get())

  debugImplementation(libs.findLibrary("androidx.compose.ui.tooling").get())
}
