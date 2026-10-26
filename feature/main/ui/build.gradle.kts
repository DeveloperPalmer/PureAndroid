plugins {
    id("compose-convention")
    id("hilt-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.main.ui"
}

dependencies {
    api(projects.core.ui)
    implementation(projects.feature.main.domain)
    implementation(projects.uikit)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.orbit.test)
}
