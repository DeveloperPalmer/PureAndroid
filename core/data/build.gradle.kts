plugins {
  id("android-library-convention")
}

android {
  namespace = "ru.sla.pure.android.core.data"
}

dependencies {
  api(projects.core.domain)
  api(libs.retrofit)
  api(libs.retrofit.kotlinx.serialization)
  api(projects.database)
  api(projects.openapiSchema)
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp)
  api(libs.okio)
}
