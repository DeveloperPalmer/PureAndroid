plugins {
  id("android-library-convention")
  id("compose-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.login.ui"
}

dependencies {
  implementation(projects.uikit)
}
