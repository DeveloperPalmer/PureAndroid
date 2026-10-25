plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs = versionCatalogs.named("libs")

// Hilt в Android-модуле: рантайм и KSP-компилятор, который генерирует код графа
dependencies {
    "implementation"(libs.findLibrary("hilt.android").get())
    ksp(libs.findLibrary("hilt.compiler").get())
}
