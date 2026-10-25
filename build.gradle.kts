// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Конвенции грузятся один раз в корне вместе с AGP, который они приносят:
    // все модули делят классы плагинов
    id("android-application-convention") apply false
    id("android-library-convention") apply false
    id("compose-convention") apply false
    id("hilt-convention") apply false
}