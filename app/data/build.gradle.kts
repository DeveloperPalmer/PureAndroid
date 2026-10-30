plugins {
    id("android-library-convention")
    id("metro-convention")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ru.sla.pure.android.app.data"
}

dependencies {
    testImplementation(libs.bundles.testing.network)
    implementation(projects.database)
    api(projects.core.data)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

}
