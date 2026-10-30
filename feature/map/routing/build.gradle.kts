plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.map.routing"
}

dependencies {
  implementation(projects.core.routing)
  api(projects.feature.map.ui)
}
