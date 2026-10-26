plugins {
    id("hilt-convention")
    // Только компилятор Compose: рантайм и viewModel { } приходят из core:ui
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
    api(projects.core.ui)

    api(libs.androidx.navigation3.runtime)
    api(libs.kotlinx.coroutines.core)
}
