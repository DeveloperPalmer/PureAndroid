// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.spotless)
  alias(libs.plugins.ksp) apply false
  // Конвенции грузятся один раз в корне вместе с AGP, который они приносят:
  // все модули делят классы плагинов
  id("android-application-convention") apply false
  id("android-library-convention") apply false
  id("compose-convention") apply false
  id("metro-convention") apply false
  id("routing-convention") apply false
  id("kotlin-convention") apply false
}

spotless {
  lineEndings = com.diffplug.spotless.LineEnding.UNIX
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**", "**/.gradle/**", "**/.git/**")
    ktlint(libs.versions.ktlint.get())
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude("**/build/**", "**/.gradle/**", "**/.git/**")
    ktlint(libs.versions.ktlint.get())
    trimTrailingWhitespace()
    endWithNewline()
  }
}

// On Android, detektMain aggregates all variants; select only debug here.
fun Project.mainDetektTasks() = tasks.matching { task ->
  val android = pluginManager.hasPlugin("com.android.application") ||
    pluginManager.hasPlugin("com.android.library")
  task.name == if (android) "detektDebug" else "detektMain"
}

subprojects {
  mainDetektTasks().configureEach {
    mustRunAfter(rootProject.tasks.named("spotlessApply"))
  }
}

tasks.register("prePushCheck") {
  group = "verification"
  description = "Formats Kotlin sources and runs Detekt for Android debug and JVM main sources."
  dependsOn("spotlessApply")
  dependsOn(subprojects.map { it.mainDetektTasks() })
}
