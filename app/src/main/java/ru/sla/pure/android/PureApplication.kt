package ru.sla.pure.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Точка входа DI. `@HiltAndroidApp` генерирует корневой компонент Hilt (`SingletonComponent`)
 * и базовый класс `Hilt_PureApplication`, который создаёт этот компонент в `onCreate`.
 * Плагин Hilt подменяет родителя класса на сгенерированный после компиляции.
 */
@HiltAndroidApp
class PureApplication : Application()
