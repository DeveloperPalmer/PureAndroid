plugins {
    id("hilt-convention")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
    api(projects.core.ui)

    api(libs.androidx.navigation3.runtime)
    api(libs.kotlinx.coroutines.core)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
}
