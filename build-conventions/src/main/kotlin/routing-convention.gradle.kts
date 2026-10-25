plugins {
    id("android-library-convention")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = versionCatalogs.named("libs")

// Модуль routing фичи: только ключи навигации. Ключ — @Serializable класс с NavKey, поэтому
// Navigation 3 сохраняет стек при пересоздании Activity и смерти процесса. Обе библиотеки
// в api: ключи публичные, и модулям, которые к ним навигируют, нужны NavKey и сериализация
dependencies {
    api(libs.findLibrary("androidx.navigation3.runtime").get())
    api(libs.findLibrary("kotlinx.serialization.core").get())
}
