plugins {
    id("compose-convention")
    id("hilt-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.cities.ui"
}

dependencies {
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
}
