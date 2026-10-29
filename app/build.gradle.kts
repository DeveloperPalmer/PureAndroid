plugins {
    id("android-application-convention")
}

android {
    namespace = "ru.sla.pure.android"

    defaultConfig {
        applicationId = "ru.sla.pure.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(projects.uikit)
    implementation(projects.core.routing)
    implementation(projects.app.data)
    implementation(projects.feature.main.routing)
    implementation(projects.feature.map.routing)
}
