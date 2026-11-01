import com.android.build.api.dsl.CommonExtension

plugins {
  id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")

extensions.configure<CommonExtension> {
  buildFeatures.compose = true
}

dependencies {
  val bomConfiguration = if (pluginManager.hasPlugin("com.android.library")) {
    "api"
  } else {
    "implementation"
  }
  add(bomConfiguration, platform(libs.findLibrary("androidx.compose.bom").get()))
  add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
}
