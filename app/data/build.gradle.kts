plugins {
    id("android-library-convention")
    id("metro-convention")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ru.sla.pure.android.app.data"
}

dependencies {
    implementation(projects.database)
    api(projects.core.data)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
}
