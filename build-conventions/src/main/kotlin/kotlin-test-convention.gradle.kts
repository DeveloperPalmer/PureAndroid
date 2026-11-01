import gradle.kotlin.dsl.accessors._4b8206f8a6e8886e9c69f593219ab709.versionCatalogs
import org.gradle.api.tasks.testing.Test

val libs = versionCatalogs.named("libs")

dependencies {
  add("testImplementation", libs.findBundle("testing.unit").get())
}

// Юнит-тесты бегут на JUnit 5 (JUnit Platform)
tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
