plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.map.ui"
}

dependencies {
  api(projects.uikit)
}
