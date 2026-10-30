plugins {
  id("routing-convention")
}

android {
  namespace = "ru.sla.pure.android.feature.main.routing"
}

dependencies {
  implementation(projects.core.routing)
  api(projects.feature.main.data)
  api(projects.feature.main.domain)
  api(projects.feature.main.ui)
}
