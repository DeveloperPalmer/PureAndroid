plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.data"
}

dependencies {
  implementation(projects.core.data)
  implementation(projects.feature.main.domain)
}
