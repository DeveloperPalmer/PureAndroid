plugins {
    id("android-library-convention")
    id("metro-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.main.data"
}

dependencies {
    implementation(projects.openapiSchema)
    implementation(projects.feature.main.domain)

    testImplementation(projects.app.data)
    testImplementation(libs.okhttp)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
