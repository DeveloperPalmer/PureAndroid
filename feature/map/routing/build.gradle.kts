plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.map.routing"
}

dependencies {
  api(projects.feature.map.ui)
  implementation(projects.core.routing)
}
