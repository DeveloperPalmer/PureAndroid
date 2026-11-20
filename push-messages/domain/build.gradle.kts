plugins {
  id("android-library-convention")
  id("kotlin-parcelize")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.pushmessages.domain"
}

dependencies {
  api(projects.core.domain)
}
