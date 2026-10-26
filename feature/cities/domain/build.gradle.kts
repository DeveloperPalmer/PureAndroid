plugins {
    id("kotlin-convention")
}

dependencies {
    // api: javax.inject и coroutines приходят из core:domain и видны в публичном API модуля
    api(projects.core.domain)
}
