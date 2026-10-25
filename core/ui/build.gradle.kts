plugins {
    id("android-library-convention")
}

android {
    namespace = "ru.sla.pure.android.core.ui"
}

dependencies {
    // api: BaseViewModel наследует ViewModel и OrbitContainerHost, а наследникам нужны orbitContainer,
    // intent и reduce — всё это часть публичного API модуля
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.orbit.core)
    api(libs.orbit.viewmodel)
}
