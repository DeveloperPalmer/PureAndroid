plugins {
  id("kotlin-convention")
  id("di-convention")
}

dependencies {
  implementation(projects.core.domain)
  implementation(libs.paging.common)
}
