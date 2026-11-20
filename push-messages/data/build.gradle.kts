plugins {
  id("android-library-convention")
  id("di-convention")
}

android {
  namespace = "ru.sla.pure.android.pushmessages.data"
}

dependencies {
  api(projects.pushMessages.domain)
  implementation(projects.buildConfig)
  implementation(projects.core.data)
  implementation(projects.feature.main.data)
}
