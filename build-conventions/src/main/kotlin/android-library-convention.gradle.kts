plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("detekt-convention")
}

android {
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    minSdk = 26
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
