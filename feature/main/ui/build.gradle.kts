plugins {
    id("compose-convention")
    id("metro-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.main.ui"
}

dependencies {
    api(projects.core.ui)
    implementation(projects.feature.main.domain)
    implementation(projects.uikit)
    implementation(libs.metrox.viewmodel)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.orbit.test)
}
