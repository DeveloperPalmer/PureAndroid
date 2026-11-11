plugins {
  id("android-library-convention")
  id("compose-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.app.routing"
}

dependencies {
  api(projects.core.routing)
  api(projects.feature.app.ui)
  api(projects.feature.login.routing)
  api(projects.feature.main.routing)
}
