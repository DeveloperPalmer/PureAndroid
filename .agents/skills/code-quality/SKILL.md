---
name: code-quality
description: Правила Gradle-конфигурации, конвенций модулей и проверок качества PureAndroid. Применяй при изменении конфигурации и подготовке или выполнении явно запрошенных проверок.
---

# Проверки и правила Gradle

## Проверки

```powershell
.\gradlew.bat spotlessCheck   # проверить форматирование Kotlin и Gradle Kotlin DSL
.\gradlew.bat spotlessApply   # исправить форматирование
.\gradlew.bat prePushCheck    # применить форматирование, запустить Detekt и юнит-тесты
```

`prePushCheck` запускает `detektDebug` и `testDebugUnitTest` в модулях, где эти задачи существуют. JVM-тесты и инструментальные UI-тесты запускаются отдельно.
Gradle-сборки, синхронизацию, проверки и тесты запускает разработчик. Агент запускает их только по отдельной просьбе.

Правила форматирования хранятся в [`.editorconfig`](../../../.editorconfig), настройки Detekt — в [`detekt-config.yml`](../../../detekt-config.yml).

## Конвенции модулей

В блоке `plugins` первой явно объявляем конвенцию платформы. В модулях с тестами отдельно подключаем тестовую конвенцию.

| Модуль | Базовая конвенция | Тестовая конвенция |
| --- | --- | --- |
| Kotlin/JVM | `kotlin-convention` | `kotlin-test-convention` |
| Android-библиотека | `android-library-convention` | `android-test-convention` |
| Android-приложение | `android-application-convention` | `android-test-convention` |

В библиотеках с Compose явно подключаем `compose-convention`, с Metro — `di-convention`.

`kotlin-test-convention` подключает набор `testing-unit` с JUnit 5, библиотеками корутин, MockK и Turbine для локальных тестов. `android-test-convention` применяет `kotlin-test-convention`, настраивает инструментальные тесты и добавляет MockWebServer и Orbit Test для локальных тестов. В модулях с `compose-convention` она подключает `:core:testing` с Compose Test и Kaspresso, а в Compose-библиотеках также добавляет ViewModel Compose и Android MockK для изолированных экранных тестов.

`android-test-convention` включает `testFixtures` по умолчанию в Android-библиотеках. Для fixtures Compose-библиотек она подключает `:app:testing` и JSON. Зависимости конкретных сценариев, например типы своего domain-модуля, Paging Testing и DI-провайдеры из `:app:data`, объявляем в использующем их модуле.

## Зависимости

- Каждый модуль фичи явно подключает базу своего слоя. Для domain это `:core:domain`, для data — `:core:data`, для UI — `:uikit`, для routing — `:core:routing`. Domain остаётся JVM-модулем.
- `:uikit` предоставляет `:core:ui` и `:core:resources`. Библиотеки, которые уже предоставляет базовый модуль слоя, повторно не объявляем.
- По умолчанию используем `implementation`. Выбираем `api` для типов публичных сигнатур и намеренного реэкспорта. Так routing-модуль предоставляет приложению модули фичи для сборки графа Metro.
- Версии библиотек, проектных плагинов и bundles храним в [каталоге версий](../../../gradle/libs.versions.toml). Версии settings-плагинов указываем литералами в `settings.gradle.kts`, поскольку Gradle [не поддерживает](https://docs.gradle.org/current/userguide/version_catalogs.html) использование плагинов из каталога в settings-файле. Ключи версий пишем в camelCase, имена библиотек, плагинов и bundles — в kebab-case. В bundle объединяем зависимости для одной конфигурации. Compose BOM подключаем через `platform(...)`.
- В `dependencies` разделяем пустой строкой основные зависимости, debug, локальные и инструментальные тесты. В основной группе сначала указываем `api`, затем `implementation`.
- При обновлении версий и ревью таких изменений применяем [правило обновления зависимостей](../dependency-upgrades/SKILL.md).
