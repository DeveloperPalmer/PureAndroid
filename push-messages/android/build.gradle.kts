plugins {
  id("android-library-convention")
}

android {
  namespace = "ru.sla.pure.android.pushmessages.android"
}

dependencies {
  api(projects.pushMessages.data)
  api(libs.firebase.messaging)
}
