plugins {
  id("kotlin-convention")
  id("kotlin-test-convention")
  id("di-convention")
}

dependencies {
  implementation(projects.core.domain)
  implementation(libs.paging.common)

  testImplementation(libs.paging.testing)
}
