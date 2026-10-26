plugins {
    id("kotlin-convention")
    id("metro-convention")
}

dependencies {
    // api: coroutines приходят из core:domain и видны в публичном API модуля
    api(projects.core.domain)
}
