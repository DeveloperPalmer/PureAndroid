plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// Hilt в Android-модуле: рантайм и KSP-компилятор, который генерирует код графа
dependencies {
    val libs = versionCatalogs.named("libs")

    add("implementation", libs.findLibrary("hilt.android").get())
    add("ksp", libs.findLibrary("hilt.compiler").get())
}
