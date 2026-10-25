plugins {
    id("android-library-convention")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs = versionCatalogs.named("libs")

// Android-библиотека с Hilt: рантайм и KSP-компилятор, который генерирует код графа
dependencies {
    implementation(libs.findLibrary("hilt.android").get())
    ksp(libs.findLibrary("hilt.compiler").get())
}
