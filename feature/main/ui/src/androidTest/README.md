# UI-тесты CharacterDetails

Тесты используют существующие Kaspresso 1.6.1, JUnit 5 и Compose Test API v2.
Запускать на подключённом устройстве или эмуляторе с API 26+.

```powershell
.\gradlew.bat :feature:main:ui:connectedDebugAndroidTest `
  '-Pandroid.testInstrumentationRunnerArguments.package=ru.sla.pure.android.feature.main.ui.test.characterdetails'
```

Команда запускает все 12 проверок CharacterDetails: 6 компонентных, 5 экранных и 1 стресс-тест.

HTML-отчёт: `feature/main/ui/build/reports/androidTests/connected/debug/index.html`.

## Покрытие

- `CharacterDetailsContentTest`: загрузка со старыми данными, `Ready` без данных,
  четыре поля персонажа, одинаковые значения происхождения и текущего места,
  ошибка со старыми данными, однократный вызов retry, возврат во всех состояниях.
- `CharacterDetailsScreenTest`: настоящие `CharacterDetailsScreen`, ViewModel и MainModel;
  загрузка нужного id, мгновенный ответ, ошибка соединения → retry → успех,
  ошибка сервера → retry → другая ошибка → retry → успех, событие закрытия во время загрузки.
- `CharacterDetailsScreen`: локаторы и повторяющиеся проверки состояния экрана.
  Проверяется и наличие ожидаемых элементов, и отсутствие элементов других состояний.

`CharacterDetailsStressTest` проверяет многократное открытие деталей и также входит
в указанную команду. Число повторов можно изменить аргументом `detailsIterations`.

Тест мгновенного ответа обнаружил потерю результата при открытии экрана: `asLceState`
и `successResults` подписываются отдельно, а стандартный `startOnSubscribe` ждёт только
одного подписчика результата. Во ViewModel используется штатный
`StartScheduled.Lazily(minResultsSubscribers = 2)`, чтобы загрузка начиналась после обеих
подписок. Replay старого результата не включается, поскольку модель общая для разных персонажей.

## Принятые подходы

- Page Object на стандартном `ComposeScreen` с дочерними `KNode`, ограниченными корнем экрана.
  Для действий используется `KNode` из Kaspresso Compose Support.
- Сценарии оформлены через `TestCase.run` и именованные `step`. Блок Kaspresso находится
  внутри Compose test environment: во время обработки ошибки экран ещё открыт для диагностики.
- Кнопки находятся по строковому ресурсу и семантике нажатия, индикатор — по progress semantics.
  Полям даны стабильные test tags: происхождение и текущее место могут иметь одинаковый текст.
  В тестах нет зависимости от языка интерфейса, координат и индексов дочерних элементов.
- Изолированные проверки получают состояние и callbacks через `CharacterDetailsContent`.
  Интеграционные сценарии подменяют только `CharacterRepository`; ViewModel не мокается.
- Каждый тест получает новый репозиторий и scope. `CompletableDeferred` позволяет удерживать
  загрузку до явного ответа теста, без `sleep`, таймеров и реальной сети.
- UI синхронизируется средствами Compose и штатными перехватчиками Kaspresso.
  Для запросов используется `coVerify` с ограниченным ожиданием, для событий навигации — `waitUntil`.
- ViewModel принадлежит Activity и очищается при её закрытии; scope модели отменяется в `@AfterEach`.

## Общая инфраструктура

Все сценарии CharacterDetails принадлежат этому модулю. Page Object `CharacterDetailsScreen`
описывает KNode, а production-компоненты используют константы `CharacterDetailsTestTags`.
Новый тестируемый экран добавляет собственный Page Object и собственные теги.

Общий `BaseTestCase` находится в `:core:testing` и подключён через androidTestImplementation.
Он запускает Kaspresso внутри Compose Test API v2, не зависит от app и не попадает в production APK.
Тестовые Application и runner в app сохранены отдельно и для этих тестов не используются.

Репозиторий подменяется строгим MockK-моком из mockk-android. coEvery/coAnswers задают ответы,
coVerify проверяет id и количество вызовов. CompletableDeferred удерживает загрузку до явного
ответа теста. Данные персонажей остаются обычными объектами, ViewModel и MainModel — настоящими.

Для Marathon соберите тестовый APK фичи и запустите CLI из корня:

```powershell
.\gradlew.bat :feature:main:ui:assembleDebugAndroidTest
marathon
```

Marathonfile настроен на библиотечный APK и обнаружение тестов на устройстве (remote parser),
поскольку используется JUnit 5. Запуск самим Marathon CLI в текущем окружении не проверен.
Результаты сохраняются локально в build/reports/marathon. Отправки в Jira нет.

Официальные рекомендации:

- [Kaspresso: Compose Support](https://kasperskylab.github.io/Kaspresso/Wiki/Jetpack_Compose/)
- [Android: изолированное тестирование composables](https://developer.android.com/develop/ui/compose/testing/common-patterns)
- [Android: синхронизация UI-тестов](https://developer.android.com/develop/ui/compose/testing/synchronization)
