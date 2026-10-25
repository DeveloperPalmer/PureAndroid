// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Конвенции грузятся один раз в корне вместе с AGP, который они приносят:
    // все модули делят классы плагинов
    id("android-application-convention") apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}