plugins {
    id("android-library-convention")
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.sla.pure.android.database"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    androidTestImplementation(libs.bundles.testing.android)
    api(libs.room.runtime)
    ksp(libs.room.compiler)
}
