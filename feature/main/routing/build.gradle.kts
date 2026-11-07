plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.routing"
}

dependencies {
  api(projects.feature.main.data)
  api(projects.feature.main.domain)
  api(projects.feature.main.ui)
  implementation(projects.core.routing)
}
