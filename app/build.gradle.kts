plugins {
    id("android-application-convention")
}

android {
    namespace = "ru.sla.pure.android"

    defaultConfig {
        applicationId = "ru.sla.pure.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(projects.core.ui)
    implementation(projects.uikit)
    implementation(projects.core.routing)

    // Приложение подключает только routing фич: ключи вкладок нужны нижней панели, а записи
    // и переходы Hilt собирает из их @IntoSet-модулей. Модули ui приходят через routing
    // транзитивно и в коде приложения недоступны
    implementation(projects.feature.cities.routing)
    implementation(projects.feature.map.routing)
}
