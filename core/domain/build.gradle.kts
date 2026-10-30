plugins {
  id("kotlin-convention")
}

dependencies {
  api(libs.remo)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.serialization.core)
  api(libs.metro.runtime)
}
