plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.auth.session.data"
}

dependencies {
  api(projects.authSession.domain)
  implementation(projects.core.data)
}
