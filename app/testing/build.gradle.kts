plugins {
  id("android-library-convention")
}

android {
  namespace = "ru.sla.pure.android.testing"
}

dependencies {
  api(projects.core.testing)
  api(libs.junit.jupiter.api)
  api(libs.kotlinx.coroutines.test)
  api(libs.androidx.test.monitor)
  api(libs.okhttp.mockwebserver)
}
