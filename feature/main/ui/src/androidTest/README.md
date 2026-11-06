# UI-тесты feature main

Модуль использует Kaspresso 1.6.1, JUnit 5 и Compose Test API v2. Запускать на устройстве или эмуляторе
с API 26+.

## Изолированные тесты в androidTest

Все 12 проверок экрана деталей собраны в `CharacterDetailsScreenTest`.

```powershell
.\gradlew.bat :feature:main:ui:connectedDebugAndroidTest `
  '-Pandroid.testInstrumentationRunnerArguments.class=ru.sla.pure.android.feature.main.ui.test.characterdetails.CharacterDetailsScreenTest'
```

Весь изолированный набор модуля, включая `MainScreenTest`:

```powershell
.\gradlew.bat :feature:main:ui:connectedDebugAndroidTest
```

XML-отчёт: `feature/main/ui/build/outputs/androidTest-results/connected/debug/*.xml`.
HTML-отчёт: `feature/main/ui/build/reports/androidTests/connected/debug/index.html`.
Количество тестов нужно сверять с XML, а не только с успешным завершением Gradle.

### Покрытие CharacterDetails

- 6 компонентных проверок состояний, полей, retry и возврата.
- 5 экранных проверок с настоящими ViewModel и MainModel.
- `opensCharacterDetailsRepeatedly` проверяет многократное открытие деталей. Аргумент `detailsIterations` меняет число повторов.

Здесь подменяется только `CharacterRepository` через MockK. ViewModel и MainModel настоящие. Это осознанно
более низкий уровень, чем app-level E2E: `CompletableDeferred` управляет ответами и позволяет точно проверить
loading, последовательность ошибок и количество вызовов без реальной сети.

Page Object ограничен корнем экрана, кнопки находятся по строковым ресурсам и семантике нажатия, поля — по
стабильным `CharacterDetailsTestTags`. Проверяется как наличие элементов текущего состояния, так и отсутствие
элементов остальных состояний. Тесты не зависят от координат, индексов дочерних элементов и языка интерфейса.

`CharacterDetailsViewModel` использует `StartScheduled.Lazily(minResultsSubscribers = 2)`: обе подписки должны
быть установлены до старта загрузки, иначе мгновенный результат мог быть потерян. Replay старого результата
не используется, поскольку MainModel общая для разных персонажей.

## Общие fixtures и app-level E2E

`android-test-convention` включает `src/testFixtures` и подключает общий рантайм E2E и JSON.
Модуль фичи объявляет зависимость от своего domain. В fixtures находятся:

- `screen/characterdetails/CharacterDetailsScreen.kt` и `Mock.kt` — Page Object и общие данные;
- `screen/main/MainScreen.kt` — общие взаимодействия со списком;
- `test/CharacterHttpFixtures.kt` — JSON-контракты ответов;
- `test/MainFlowE2ETestCase.kt` — абстрактный класс с двумя app-level E2E-сценариями.

Собственные `testFixtures` автоматически входят и в feature test APK. Поэтому E2E-сценарии объявлены
в абстрактном классе: runner не запускает его при `:feature:main:ui:connectedDebugAndroidTest`.
Конкретный `ru.sla.pure.android.testing.feature.main.MainFlowE2ETest` находится в `app/src/androidTest` и наследует
сценарии. Изолированный набор содержит 23 теста, app E2E — 2. Запуск:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest `
  '-Pandroid.testInstrumentationRunnerArguments.class=ru.sla.pure.android.testing.feature.main.MainFlowE2ETest'
```

Проверяются список → детали → назад и HTTP 503 в деталях → retry → успех. Используются настоящие MainActivity,
навигация, ViewModel, MainModel, repository, Retrofit, сериализация, мапперы и Room DAO. Подменяются только URL
на `MockWebServer` и файловая Room на in-memory. В E2E нет mock-репозитория и `setContent`.

При добавлении fixtures нельзя подключать feature data/implementation к `:app:testing`: общий runtime не должен
зависеть от фич. Feature приносит собственные Page Objects, данные и сценарии через свой `testFixtures` artifact.

## Marathon для изолированных тестов

```powershell
.\gradlew.bat :feature:main:ui:assembleDebugAndroidTest
marathon
```

Корневой `Marathonfile` настроен на библиотечный test APK и remote parser для JUnit 5. Результаты —
`build/reports/marathon`. App E2E использует отдельный `Marathonfile.app-e2e`; см. `app/src/androidTest/README.md`.
Marathon CLI в текущем окружении отсутствует, поэтому реальные запуски этих конфигураций не проверены.

Официальные рекомендации:

- [Kaspresso: Compose Support](https://kasperskylab.github.io/Kaspresso/Wiki/Jetpack_Compose/)
- [Android: изолированное тестирование composables](https://developer.android.com/develop/ui/compose/testing/common-patterns)
- [Android: синхронизация UI-тестов](https://developer.android.com/develop/ui/compose/testing/synchronization)
