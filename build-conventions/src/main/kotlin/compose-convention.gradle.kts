import com.android.build.api.dsl.CommonExtension

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")

// Compose в Android-модуле: приложении или библиотеке. Применяется после конвенции AGP,
// потому что настраивает расширение android, которое создаёт AGP
extensions.configure<CommonExtension> {
    buildFeatures.compose = true
}

// Базовый набор Compose. Версии задаёт BOM, поэтому у библиотек их нет
dependencies {
    "implementation"(platform(libs.findLibrary("androidx.compose.bom").get()))
    "implementation"(libs.findLibrary("androidx.compose.ui").get())
    "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
    "implementation"(libs.findLibrary("androidx.compose.material3").get())
    "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
}
