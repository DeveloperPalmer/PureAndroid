plugins {
  id("com.android.library")
  id("detekt-convention")
}

// Общие настройки Android-библиотеки (модули фич и ядра). Модуль задаёт только namespace
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
