plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(libs.detekt.plugin)
  // Плагины, которые применяют конвенции, подключаются как зависимости: так их версии
  // задаются здесь, а не в build-файлах модулей
  implementation(plugin(libs.plugins.android.application))
  implementation(plugin(libs.plugins.android.library))
  implementation(plugin(libs.plugins.metro))
  implementation(plugin(libs.plugins.kotlin.jvm))
  implementation(plugin(libs.plugins.kotlin.compose))
  implementation(plugin(libs.plugins.kotlin.serialization))
}

// Плагин из [plugins] каталога как обычная зависимость. Каждый плагин публикуется с артефактом-маркером
// <id>:<id>.gradle.plugin — по нему же плагин находит и блок plugins {}
fun plugin(plugin: Provider<PluginDependency>): Provider<String> = plugin.map {
  "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
}
