plugins {
    id("routing-convention")
}

android {
    namespace = "ru.sla.pure.android.feature.main.routing"
}

dependencies {
    implementation(projects.core.routing)
    implementation(projects.feature.main.domain)
    implementation(projects.feature.main.ui)
}
