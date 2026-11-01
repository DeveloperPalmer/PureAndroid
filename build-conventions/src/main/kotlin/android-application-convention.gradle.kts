plugins {
  id("com.android.application")
  id("compose-convention")
  id("detekt-convention")
  id("di-convention")
}

android {
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    minSdk = 26
    targetSdk = 37
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
}
