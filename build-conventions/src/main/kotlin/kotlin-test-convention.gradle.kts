dependencies {
  add("testImplementation", versionCatalogs.named("libs").findBundle("testing.unit").get())
}
