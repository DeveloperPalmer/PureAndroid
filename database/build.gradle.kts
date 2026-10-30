plugins {
  id("android-library-convention")
  id("android-library-test-convention")
  alias(libs.plugins.ksp)
}

android {
  namespace = "ru.sla.pure.android.database"
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
  api(projects.core.domain)
  api(libs.room.runtime)
  api(libs.room.common)
  api(libs.androidx.sqlite)

  ksp(libs.room.compiler)
}
