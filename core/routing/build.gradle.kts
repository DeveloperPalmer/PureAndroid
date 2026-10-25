plugins {
    id("hilt-convention")
}

android {
    namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
    api(projects.core.ui)

    api(libs.androidx.navigation3.runtime)
    api(libs.kotlinx.coroutines.core)
}
