plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.lib.debugpanel.routing"
}

dependencies {
  api(projects.lib.debugPanel.domain)
  api(projects.lib.debugPanel.ui)
  implementation(libs.androidx.core.runtime)
  implementation(projects.core.resources)
  implementation(projects.core.routing)
}
