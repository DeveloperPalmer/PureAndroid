plugins {
  id("compose-convention")
  id("metro-convention")
  id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = versionCatalogs.named("libs")

dependencies {
  api(libs.findLibrary("androidx.navigation3.runtime").get())
  implementation(libs.findLibrary("kotlinx.serialization.core").get())
}
