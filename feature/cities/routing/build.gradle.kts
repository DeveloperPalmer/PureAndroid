plugins {
    id("routing-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.cities.routing"
}

dependencies {
    implementation(projects.core.routing)
    implementation(projects.feature.cities.domain)
    implementation(projects.feature.cities.ui)
}
