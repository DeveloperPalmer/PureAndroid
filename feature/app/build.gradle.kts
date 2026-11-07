plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.app"
}

dependencies {
  api(projects.core.routing)
  api(projects.feature.main.routing)
  api(projects.feature.map.routing)
  implementation(libs.androidx.annotation)
}
