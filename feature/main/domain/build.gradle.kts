plugins {
  id("kotlin-convention")
  id("kotlin-test-convention")
  id("di-convention")
}

dependencies {
  api(projects.authSession.domain)
  implementation(projects.core.domain)
  implementation(libs.paging.common)

  testImplementation(libs.paging.testing)
}
