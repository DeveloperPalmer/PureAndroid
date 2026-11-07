import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension

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
  add("androidTestRuntimeOnly", libs.findLibrary("androidx.test.runner").get())
}

pluginManager.withPlugin("com.android.library") {
  extensions.configure<LibraryExtension> {
    testFixtures.enable = true
  }
}

pluginManager.withPlugin("compose-convention") {
  dependencies {
    add("debugRuntimeOnly", libs.findLibrary("androidx.compose.ui.test.manifest").get())

    add("androidTestImplementation", project(":core:testing"))
  }
  pluginManager.withPlugin("com.android.library") {
    dependencies {
      add("androidTestImplementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
      add("androidTestImplementation", libs.findLibrary("mockk.android").get())

      add("testFixturesApi", project(":app:testing"))
      add("testFixturesImplementation", libs.findLibrary("kotlinx.serialization.json").get())
    }
  }
}
