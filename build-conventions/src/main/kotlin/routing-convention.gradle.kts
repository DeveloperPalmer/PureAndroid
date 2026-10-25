plugins {
    id("compose-convention")
    id("hilt-convention")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = versionCatalogs.named("libs")

// Модуль routing фичи: ключи навигации, записи с экранами модуля ui и переходы по событиям этих
// экранов. Ключ — @Serializable класс с NavKey, поэтому Navigation 3 сохраняет стек при пересоздании
// Activity и смерти процесса. Обе библиотеки в api: ключи публичные, и модулям, которые
// к ним навигируют, нужны NavKey и сериализация
dependencies {
    api(libs.findLibrary("androidx.navigation3.runtime").get())
    api(libs.findLibrary("kotlinx.serialization.core").get())
}
