plugins {
  id("kotlin-convention")
  id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
  api(projects.core.domain)
  api(libs.retrofit)
  api(libs.kotlinx.serialization.json)
}
