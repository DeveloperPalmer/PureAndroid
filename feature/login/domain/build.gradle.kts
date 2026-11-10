plugins {
  id("kotlin-convention")
  id("di-convention")
}

dependencies {
  implementation(projects.core.domain)
  implementation(projects.authSession.domain)
}
