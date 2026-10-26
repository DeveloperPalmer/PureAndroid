plugins {
    id("hilt-convention")
    // Только компилятор Compose: compose-convention подключил бы весь UI-стек, а здесь нужны
    // только рантайм и viewModel { }
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.sla.pure.android.core.routing"
}

dependencies {
    api(projects.core.ui)

    api(libs.androidx.navigation3.runtime)
    api(libs.kotlinx.coroutines.core)

    // api: записи флоу — @Composable-лямбды, а ViewModel экранов в них создаются через viewModel { }
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.viewmodel.compose)
}
