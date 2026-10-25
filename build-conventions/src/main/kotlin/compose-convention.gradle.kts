plugins {
    id("android-library-convention")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")

// Android-библиотека с UI на Compose. Версии библиотек Compose задаёт BOM
android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.findLibrary("androidx.compose.bom").get()))
    implementation(libs.findBundle("compose").get())
    debugImplementation(libs.findLibrary("androidx.compose.ui.tooling").get())
}
