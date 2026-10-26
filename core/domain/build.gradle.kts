plugins {
    id("kotlin-convention")
}

dependencies {
    // api: общая база domain-слоя, её получает каждый модуль, который подключил core:domain.
    // Аннотации javax.inject нужны для маркеров скоупа и @Inject, coroutines — для StateFlow в моделях
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)
}
