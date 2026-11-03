import com.android.build.api.dsl.CommonExtension

plugins {
  id("kotlin-test-convention")
  id("de.mannodermaus.android-junit")
}

val libs = versionCatalogs.named("libs")

extensions.configure<CommonExtension> {
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

dependencies {
  add("testImplementation", libs.findLibrary("okhttp.mockwebserver").get())
  add("testImplementation", libs.findLibrary("orbit.test").get())
  add("androidTestImplementation", libs.findBundle("testing.android").get())
  add("androidTestImplementation", libs.findLibrary("kaspresso").get())
  add("androidTestRuntimeOnly", libs.findLibrary("androidx.test.runner").get())
}

pluginManager.withPlugin("compose-convention") {
  dependencies {
    add("debugRuntimeOnly", libs.findLibrary("androidx.compose.ui.test.manifest").get())
    add("androidTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
    add("androidTestImplementation", libs.findLibrary("kaspresso.compose.support").get())
  }
}
