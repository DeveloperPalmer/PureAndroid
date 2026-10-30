plugins {
    id("kotlin-convention")
    id("metro-convention")
}

dependencies {
    testImplementation(libs.bundles.testing.unit)
    api(projects.core.domain)
    api(libs.paging.common)
}
