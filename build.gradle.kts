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
  alias(libs.plugins.dependency.analysis)
}

dependencyAnalysis {
  // Report advice for review; dependency changes remain explicit decisions.
  issues {
    all {
      onAny {
        severity("warn")
      }
    }
  }
  reporting {
    printBuildHealth(true)
  }
}

tasks.named("buildHealth") {
  mustRunAfter("spotlessApply")
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
  pluginManager.apply("com.autonomousapps.dependency-analysis")
  mainDetektTasks().configureEach {
    mustRunAfter(rootProject.tasks.named("spotlessApply"))
  }
}

tasks.register("prePushCheck") {
  group = "verification"
  description = "Formats Kotlin sources, runs Detekt and reports dependency health for all modules."
  dependsOn("spotlessApply")
  dependsOn("buildHealth")
  dependsOn(subprojects.map { it.mainDetektTasks() })
}
