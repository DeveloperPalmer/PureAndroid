plugins {
    id("compose-convention")
    id("hilt-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.cities.ui"
}

dependencies {
    api(projects.core.ui)
    implementation(projects.uikit)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.orbit.test)
}
