plugins {
  id("android-library-convention")
  id("android-library-test-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.app.data"
}

dependencies {
  api(projects.core.data)
  api(projects.database)
}
