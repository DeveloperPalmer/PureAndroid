# Проверки и правила Gradle

## Проверки

```powershell
.\gradlew.bat spotlessCheck   # проверить форматирование Kotlin и Gradle Kotlin DSL
.\gradlew.bat spotlessApply   # исправить форматирование
.\gradlew.bat prePushCheck    # применить форматирование, запустить Detekt и юнит-тесты
```

`prePushCheck` запускает `detektDebug` и `testDebugUnitTest` в модулях, где эти задачи существуют. JVM-тесты и инструментальные UI-тесты запускаются отдельно.
Gradle-сборки, синхронизацию, проверки и тесты запускает разработчик. Агент запускает их только по отдельной просьбе.

Правила форматирования хранятся в [`.editorconfig`](../.editorconfig), настройки Detekt — в [`detekt-config.yml`](../detekt-config.yml).

## Конвенции модулей

В блоке `plugins` первой явно объявляем конвенцию платформы. В модулях с тестами отдельно подключаем тестовую конвенцию.

| Модуль | Базовая конвенция | Тестовая конвенция |
| --- | --- | --- |
| Kotlin/JVM | `kotlin-convention` | `kotlin-test-convention` |
| Android-библиотека | `android-library-convention` | `android-test-convention` |
| Android-приложение | `android-application-convention` | `android-test-convention` |

В библиотеках с Compose явно подключаем `compose-convention`, с Metro — `di-convention`.

`kotlin-test-convention` подключает набор `testing-unit` с JUnit 5, библиотеками корутин, MockK и Turbine для локальных тестов. `android-test-convention` применяет `kotlin-test-convention`, настраивает инструментальные тесты и добавляет MockWebServer и Orbit Test. В модулях с `compose-convention` она также подключает библиотеки для Compose UI-тестов. Зависимости конкретных сценариев, например Paging Testing и DI-провайдеры из `:app:data`, объявляем в использующем их модуле.

## Зависимости

- Каждый модуль фичи явно подключает базу своего слоя. Для domain это `:core:domain`, для data — `:core:data`, для UI — `:uikit`, для routing — `:core:routing`. Domain остаётся JVM-модулем.
- `:uikit` предоставляет `:core:ui` и `:core:resources`. Библиотеки, которые уже предоставляет базовый модуль слоя, повторно не объявляем.
- По умолчанию используем `implementation`. Выбираем `api` для типов публичных сигнатур и намеренного реэкспорта. Так routing-модуль предоставляет приложению модули фичи для сборки графа Metro.
- Версии, библиотеки, плагины и bundles храним в [каталоге версий](../gradle/libs.versions.toml). Ключи версий пишем в camelCase, имена библиотек, плагинов и bundles — в kebab-case. В bundle объединяем зависимости для одной конфигурации. Compose BOM подключаем через `platform(...)`.
- В `dependencies` разделяем пустой строкой основные зависимости, debug, локальные и инструментальные тесты. В основной группе сначала указываем `api`, затем `implementation`.
