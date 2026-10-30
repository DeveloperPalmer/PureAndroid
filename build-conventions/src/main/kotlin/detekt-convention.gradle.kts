import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
  id("dev.detekt")
}

dependencies {
  detektPlugins(versionCatalogs.named("libs").findLibrary("detekt.rules.compose").get())
}

detekt {
  buildUponDefaultConfig = true
  config.setFrom(rootProject.file("detekt-config.yml"))
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = "11"
  // "exclude(**/generated/**)" doesn't work for some reason, see
  // https://github.com/detekt/detekt/issues/4127#issuecomment-1260733842
  exclude {
    it.file.absolutePath.contains("${File.separator}generated${File.separator}")
  }
  reports {
    checkstyle.required.set(false)
    html.required.set(false)
    markdown.required.set(false)
    sarif.required.set(false)
  }
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
  jvmTarget = "11"
}
