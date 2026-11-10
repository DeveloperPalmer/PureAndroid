plugins {
  id("android-library-convention")
  alias(libs.plugins.ksp)
  alias(libs.plugins.androidx.room3)
}

android {
  namespace = "ru.sla.pure.android.database"
}

room3 {
  schemaDirectory("$projectDir/schemas")
}

dependencies {
  api(projects.core.domain)
  api(libs.room3.runtime)

  ksp(libs.room3.compiler)
}
