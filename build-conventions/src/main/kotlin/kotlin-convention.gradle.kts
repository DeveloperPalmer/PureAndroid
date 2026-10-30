import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("detekt-convention")
}

// Чистый Kotlin-модуль без Android (domain-слой). Байткод той же версии, что у Android-модулей:
// иначе Gradle не даст подключить его к модулю с Java 11
java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_11
  }
}
