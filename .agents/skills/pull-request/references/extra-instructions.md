# Контекст PureAndroid

PureAndroid — Android-приложение на Kotlin и Compose с пакетом `ru.sla.pure.android`. Фактический состав модулей определяет [settings.gradle.kts](../../../../settings.gradle.kts); версии — [каталог](../../../../gradle/libs.versions.toml), SDK/JVM — [конвенции](../../../../build-conventions/src/main/kotlin). Проверяй эти файлы вместо переноса чисел из omim.

- `app/` запускает приложение; `app/data/` собирает сеть и БД; `app/routing/` содержит Navigation 3 и стеки вкладок.
- `feature/<name>/{domain,data,ui,routing}` разделяет модель, реализацию данных, экран и координацию флоу. `core/` содержит общую Kotlin-инфраструктуру. Для конкретных зависимостей читай build-файлы модулей.
- `BaseViewModel` использует Orbit. Методы действий выполняют `intent`, состояние изменяется через `reduce`, экранные эффекты — через `postSideEffect`. `FlowEventSink` передаёт события модулю routing.
- Metro связывает компоненты приложения и флоу, ViewModel и assisted-фабрики. Модели используют `ReactiveModel`/Remo и scope владельца графа.
- Сеть — Retrofit, OkHttp, kotlinx.serialization и `ApiErrorCallAdapterFactory`. Контракты API находятся в `openapi-schema/`.
- Хранение — Room 3 в `database/`; `CharacterDatabase` хранит персонажей и страницы, `SettingsDatabase` — настройки. Провайдеры находятся в `app/data/.../DatabaseModule.kt`.
- `uikit/` и `core/resources/` задают тему и ресурсы. Проверки — Spotless, Detekt, юнит-тесты, Compose/Kaspresso UI- и E2E-тесты; CI — GitHub Actions.

Первичны [AGENTS.md](../../../../AGENTS.md), код и применимые skills. При новом стеке или противоречии уточни применимость требований до их замены. Не приписывай проекту зависимости и инструменты только по совпадению имени модуля.
