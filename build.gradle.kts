// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  // Конвенции грузятся один раз в корне вместе с AGP, который они приносят:
  // все модули делят классы плагинов
  id("android-application-convention") apply false
  id("android-library-convention") apply false
  id("compose-convention") apply false
  id("di-convention") apply false
  id("kotlin-convention") apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.spotless)
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

subprojects {
  tasks.matching { it.name == "detektDebug" }.configureEach {
    mustRunAfter(rootProject.tasks.named("spotlessApply"))
  }
}

tasks.register("detektDebug") {
  dependsOn(
    subprojects.map { project ->
      project.tasks.matching { it.name == "detektDebug" }
    }
  )
}

tasks.register("testDebugUnitTest") {
  dependsOn(
    subprojects.map { project ->
      project.tasks.matching { it.name == "testDebugUnitTest" }
    }
  )
}

tasks.register("prePushCheck") {
  group = "verification"
  description = "Formats Kotlin sources, runs Detekt and unit tests."

  dependsOn("spotlessApply")
  dependsOn("detektDebug")
  dependsOn("testDebugUnitTest")
}
