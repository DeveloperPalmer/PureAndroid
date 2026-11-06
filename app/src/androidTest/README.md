# Окружение app-level E2E

В `app/src/androidTest` находятся `testing.core.TestAppRunner`, `testing.core.TestApplication` и конкретные
классы запуска E2E в `testing.feature`.
Сценарии и Page Objects принадлежат feature-модулям. Сейчас app подключает их через
`androidTestImplementation(testFixtures(projects.feature.main.ui))`, а `MainFlowE2ETest` наследует сценарии
абстрактного `MainFlowE2ETestCase`.

## Что является настоящим

E2E запускает production `MainActivity` без `setContent` и проходит путь:

```text
MainActivity → navigation → ViewModel → MainModel → CharacterRepositoryImpl
             → Retrofit / kotlinx.serialization → Room DAO
```

`TestAppGraph` исключает только `NetworkConfigurationModule` и `DatabaseModule`:

- production URL заменяется адресом локального `MockWebServer`;
- файловая Room заменяется на `Room.inMemoryDatabaseBuilder` для изоляции тестов;
- `CharacterRepository`, ViewModel, API, мапперы, DAO и остальные bindings остаются настоящими;
- MockK и feature-репозитории в тестовом окружении app не используются.

Это app-level E2E с управляемым backend, а не проверка удалённого production/staging сервера.

## Модули

- `:app:testing` содержит `AppUnderTest`, `BaseAppTestCase` и `TestBackend`. Он не зависит от feature-модулей.
- `:core:testing` содержит только общий Kaspresso/Compose `BaseTestCase`.
- `feature/main/ui/src/testFixtures` содержит Page Objects, HTTP fixtures и абстрактный `MainFlowE2ETestCase`.
- Новая Compose-фича применяет `android-test-convention` и добавляет сценарии в `src/testFixtures`.
  Конвенция включает fixtures и добавляет общие зависимости. В `app/build.gradle.kts` подключаем
  `androidTestImplementation(testFixtures(...))` и добавляем конкретный класс-наследник в `app/src/androidTest`.
  Межфичевые journeys следует выносить в отдельный journey-модуль, а не в чужую фичу.

## Запуск

Из корня проекта на подключённом устройстве или эмуляторе API 26+:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

Один класс:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest `
  '-Pandroid.testInstrumentationRunnerArguments.class=ru.sla.pure.android.testing.feature.main.MainFlowE2ETest'
```

Результат проверять по `app/build/outputs/androidTest-results/connected/debug/*.xml`: `BUILD SUCCESSFUL`
сам по себе не доказывает нужное количество обнаруженных тестов. HTML-отчёт находится в
`app/build/reports/androidTests/connected/debug/index.html`.

AndroidX Test Orchestrator включён для app. In-memory Room и `reset()` дополнительно ограничивают время
жизни состояния одним тестом.

## Marathon

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
marathon --marathonfile Marathonfile.app-e2e
```

`Marathonfile.app-e2e` использует app APK, app test APK, remote parser для JUnit 5 и два шарда. Отчёты —
`build/reports/marathon-app-e2e`. Marathon CLI в текущем окружении отсутствует, поэтому эта конфигурация
подготовлена, но её реальный запуск не проверен.

[Изолированные тесты и fixtures feature main](../../../feature/main/ui/src/androidTest/README.md).
