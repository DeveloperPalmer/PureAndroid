plugins {
    id("android-library-convention")
    id("metro-convention")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
    api(projects.core.domain)
    api(projects.core.ui)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.metrox.viewmodel.compose)

    testImplementation(libs.junit)
}
