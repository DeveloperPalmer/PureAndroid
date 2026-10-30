plugins {
    id("kotlin-convention")
    id("metro-convention")
}

dependencies {
    api(libs.remo)
    api(libs.kotlinx.coroutines.core)
}
