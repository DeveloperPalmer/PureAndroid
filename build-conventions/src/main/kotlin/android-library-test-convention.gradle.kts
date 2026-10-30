import com.android.build.api.dsl.LibraryExtension

val libs = versionCatalogs.named("libs")

extensions.configure<LibraryExtension> {
  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

pluginManager.withPlugin("compose-convention") {
  dependencies {
    add("debugRuntimeOnly", libs.findLibrary("androidx.compose.ui.test.manifest").get())
    add("androidTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
    add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test").get())
    add("androidTestRuntimeOnly", libs.findLibrary("androidx.espresso.core").get())
  }
}

dependencies {
  add("testImplementation", libs.findBundle("testing.unit").get())
  add("testImplementation", libs.findLibrary("okhttp.mockwebserver").get())
  add("testImplementation", libs.findLibrary("orbit.test").get())
  add("androidTestImplementation", libs.findBundle("testing.android").get())
  add("androidTestRuntimeOnly", libs.findLibrary("androidx.test.runner").get())

  val appDataPath = ":app:data"
  // Конвенцию использует и сам :app:data. Его тестам DI-провайдеры доступны напрямую.
  if (project.path != appDataPath) {
    add("testImplementation", project(appDataPath))
    add("androidTestImplementation", project(appDataPath))
  }
}

