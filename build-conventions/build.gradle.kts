plugins {
    `kotlin-dsl`
}

dependencies {
    // Плагины, которые применяют конвенции, подключаются как зависимости: так их версии
    // задаются здесь, а не в build-файлах модулей
    implementation(libs.android.gradle.plugin)
    implementation(libs.ksp.plugin)
    implementation(libs.hilt.plugin)
}
