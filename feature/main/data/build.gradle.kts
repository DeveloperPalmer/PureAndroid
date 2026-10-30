plugins {
    id("android-library-convention")
    id("metro-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.main.data"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.database)
    implementation(projects.openapiSchema)
    implementation(projects.feature.main.domain)

    testImplementation(projects.app.data)
    testImplementation(libs.okhttp)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(projects.app.data)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
