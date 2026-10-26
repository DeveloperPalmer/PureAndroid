plugins {
    id("android-library-convention")
    // Только компилятор Compose: compose-convention сам подключает core:ui и здесь дал бы цикл
    id("org.jetbrains.kotlin.plugin.compose")
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

    // api: WiredScreen — @Composable-контракт, а ViewModel экрана он берёт через viewModel { }
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.viewmodel.compose)

    // api: ресурсы приложения получает каждый модуль, который подключил core:ui
    api(projects.core.resources)
}
