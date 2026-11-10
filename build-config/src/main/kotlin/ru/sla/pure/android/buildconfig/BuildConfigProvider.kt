package ru.sla.pure.android.buildconfig

interface BuildConfigProvider {
  val buildType: BuildType
  val googleWebClientId: String
}
