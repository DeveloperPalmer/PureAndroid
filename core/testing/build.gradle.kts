plugins {
  id("android-library-convention")
}

android {
  namespace = "ru.sla.pure.android.core.testing"
}

dependencies {
  api(platform(libs.androidx.compose.bom))
  api(libs.androidx.compose.ui.test)
  api(libs.androidx.activity)
  api(libs.kaspresso)
  api(libs.kaspresso.compose.support)
}
