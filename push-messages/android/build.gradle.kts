plugins {
  id("android-library-convention")
}

android {
  namespace = "ru.sla.pure.android.pushmessages.android"
}

dependencies {
  api(projects.pushMessages.data)
  api(projects.pushMessages.domain)
  api(libs.firebase.messaging)
  implementation(projects.core.resources)
  implementation(libs.androidx.core.runtime)
}
