plugins {
  id("kotlin-convention")
  id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
  api(projects.core.data)
}
