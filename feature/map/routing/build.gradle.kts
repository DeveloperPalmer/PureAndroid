plugins {
  id("routing-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.map.routing"
}

dependencies {
  implementation(projects.core.routing)
  implementation(projects.feature.map.ui)
}
