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
    androidTestImplementation(libs.bundles.testing.android)
    testImplementation(libs.bundles.testing.network)
    implementation(projects.database)
    implementation(projects.openapiSchema)
    implementation(projects.feature.main.domain)

    testImplementation(projects.app.data)
    androidTestImplementation(projects.app.data)
}
