plugins {
    id("kotlin-convention")
    id("metro-convention")
}

dependencies {
    api(projects.core.domain)
}
