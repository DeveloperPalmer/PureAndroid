import com.android.build.api.dsl.LibraryExtension

plugins {
  id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")

// UI libraries are exported through uikit; compiler settings and tooling are shared here.
extensions.configure<LibraryExtension> {
  buildFeatures {
    compose = true
  }
}

dependencies {
  add("api", platform(libs.findLibrary("androidx.compose.bom").get()))
  add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
}
