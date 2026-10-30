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
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
