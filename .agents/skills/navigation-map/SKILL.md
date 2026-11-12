---
name: navigation-map
description: Навигация по PureAndroid при поиске модуля, экрана, маршрута, DI-привязки или теста.
---

# Карта проекта PureAndroid

Корневой пакет — `ru.sla.pure.android`. Точки входа приложения лежат в `app/src/main/java`, исходники библиотек — в `src/main/kotlin` соответствующего модуля. Полный список модулей и включённых сборок смотри в [settings.gradle.kts](../../../settings.gradle.kts).

## Какой модуль открыть

| Задача | Где смотреть |
| --- | --- |
| Запуск приложения, Activity, корневой граф Metro | [`:app`](../../../app/src/main), `PureApplication`, `MainActivity`, `AppComponent` и `AndroidManifest.xml` |
| Стеки, переходы и DI корневого флоу | [`:app:routing`](../../../app/routing), `AppFlowHost`, `NavigationState`, `AppNavigator`, `NavigationBarSceneDecoratorStrategy`, `SlideTransitions` |
| Граф флоу приложения и проверка сессии | [`:feature:app:routing`](../../../feature/app/routing), `AppFlowComponent` |
| Вкладки и нижняя панель | `MainEntryProviderInstaller` в [`:feature:main:routing`](../../../feature/main/routing) задаёт список вкладок. `AppNavigationBar` находится в [`:app:routing`](../../../app/routing) |
| Контракты навигации и общий DI-граф экранов одного флоу | [`:core:routing`](../../../core/routing), `Navigator`, `EntryProviderInstaller`, `FlowEntryScope`, `FlowHost`, `FlowComponent` |
| Список персонажей и экран деталей | [`:feature:main:ui`](../../../feature/main/ui/src/main), `screen/main` и `screen/characterdetails` содержат Compose-экран, ViewModel и `ViewState`. MVI на Orbit, общая база `BaseViewModel` в [`:core:ui`](../../../core/ui) |
| Профиль и выход из аккаунта | [`:feature:main:ui`](../../../feature/main/ui/src/main), `screen/profile`: `ProfileScreen` и `ProfileViewModel`. Задача `MainModel.logout` в `:feature:main:domain` вызывает `MainRepository.logout` для выхода из Google, затем сбрасывает сессию с очисткой кэша через `AuthSessionRepository`. `MainRepositoryImpl` привязан в `MainScope`. `MainFlow` передаёт `LogoutSucceeded` в `AppFlow`, который заменяет корень на экран входа |
| Ключи, экраны флоу «Главная» и обработка его событий | [`:feature:main:routing`](../../../feature/main/routing), `MainRoute`, `MainFlow`, `MainEntryProviderInstaller`, `MainFlowComponent` |
| Вход и сохранение сессии | [`:feature:login:routing`](../../../feature/login/routing), `LoginFlow` и `LoginFlowComponent`. Экран и Orbit-интенты находятся в [`:feature:login:ui`](../../../feature/login/ui), сохранение сессии проходит через `LoginModel`. |
| Вкладка «Карта» | [`:feature:map:routing`](../../../feature/map/routing) регистрирует `MapRoute.Map` через `MapEntryProviderInstaller`. [`:feature:map:ui`](../../../feature/map/ui) содержит `MapScreen`, экран-заглушку |
| Загрузка списка, Paging и контракт репозитория | [`:feature:main:domain`](../../../feature/main/domain), `MainModel`, `CharacterPagingSource`, `MainRepository` и модели `Character` и `CharacterPage`. Общие модели ошибок, `ReactiveModel` и `LazyStartTask` в [`:core:domain`](../../../core/domain) |
| Реализация репозитория и маппинг данных | [`:feature:main:data`](../../../feature/main/data), `MainRepositoryImpl`, `CharacterNetworkModule` и мапперы в `network/mapper` и `cache/mapper`. При `ConnectivityError` репозиторий пробует прочитать кэш Room |
| Настройки HTTP и подключение БД к графу | [`:app:data`](../../../app/data), `NetworkConfigurationModule` задаёт URL, `NetworkModule` настраивает OkHttp и Retrofit, `DatabaseModule` предоставляет Room и DAO |
| JSON, обработка сетевых ошибок, API и хранение | [`:core:data`](../../../core/data) содержит `ServerJson`, `ApiErrorCallAdapterFactory` и `NetworkErrorConverter`. В [`:openapi-schema`](../../../openapi-schema/README.md) лежат `CharacterApi` и JSON-модели, в [`:database`](../../../database) — `CharacterDatabase`, `CharacterDao` и сущности Room |
| Тема, цвета, типографика и общие ресурсы | [`:uikit`](../../../uikit), `AppTheme` и токены в `theme/`. Строки и иконки вкладок в [`:core:resources`](../../../core/resources/src/main/res). Общие типы состояния и маппинг UI-ошибок в `:core:ui` |

## Как проследить маршрут и DI

1. **От запуска до экрана.** `MainActivity` показывает splash, пока `AppFlow` находится на `AppRoute.InitialResolve`. При активной сессии `AppFlow` заменяет корень на `MainRoute.Main`, при неактивной — на `LoginRoute.Credentials`. `CredentialsViewModel` сохраняет Google-сессию и отправляет `LoginSucceeded`. `LoginFlow` передаёт событие `AppFlow`, который заменяет Login на Main.
2. **От клика до перехода.** `MainViewModel` отправляет `MainFlowEvent.CharacterDetailsRequested(characterId)` через `FlowEventSink`. `MainFlowComponent` связывает `FlowEventSink` с `MainFlow`. `AppFlowHost` передаёт навигатор контейнера в фабрику `MainFlowComponent`. `MainFlow` переводит событие в `Navigator.navigateTo(MainRoute.CharacterDetails(characterId))`. Запись деталей в `MainEntryProviderInstaller` передаёт ID в assisted-фабрику `CharacterDetailsViewModel`. `CharacterDetailsDismissed` вызывает `navigateBack()`.
3. **От вкладки до стека.** `MainEntryProviderInstaller` объявляет `NavigationTab` в порядке отображения. `MainFlow.onEntry()` задаёт стартовую вкладку. `NavigationState` держит отдельный стек для каждой вкладки. Назад с деталей убирает верхнюю запись, назад с корня карты возвращает на стартовую вкладку. Нижняя панель включается метаданными `showNavigationBar()` у записи.
4. **От записи до ViewModel.** `MainEntryProviderInstaller` объявляет `flow(route = MainRoute.Flow, ...)` с `MainFlowComponent`. `FlowComponentViewModel` хранит общий граф в хранилище корневой записи и отменяет scope при очистке. Начало и завершение флоу видны в Logcat с тегом `FlowLifecycle`. У каждой записи своё хранилище ViewModel. В `ui` привязку `MainViewModel` можно найти по `@ViewModelKey`, а фабрику деталей — по `@ManualViewModelAssistedFactoryKey`. Для реализации интерфейса ищи `@ContributesBinding` или `@Binds`, для провайдера — `@Provides`.

## Где искать тесты и настройки сборки

- **Юнит-тесты** лежат рядом с владельцем поведения в `src/test`. Например, `CharacterPagingSourceTest` в `:feature:main:domain`, `MainViewModelTest` в `:feature:main:ui`, `NetworkErrorConverterTest` в `:core:data`.
- **Изолированные UI-тесты** лежат в [`feature/main/ui/src/androidTest`](../../../feature/main/ui/src/androidTest/README.md). Page Objects, HTTP fixtures и общий сценарий `MainFlowE2ETestCase` находятся в [`src/testFixtures`](../../../feature/main/ui/src/testFixtures).
- **E2E приложения** запускает `MainFlowE2ETest` в [`app/src/androidTest`](../../../app/src/androidTest/README.md). `TestApplication` использует локальный MockWebServer и Room в памяти. [`:app:testing`](../../../app/testing) содержит общее E2E-окружение, [`:core:testing`](../../../core/testing) — базу тестов Kaspresso/Compose.
- **Сборка** использует [конвенции Kotlin DSL](../../../build-conventions/src/main/kotlin), [каталог версий](../../../gradle/libs.versions.toml) и [проверки PR](../../../.github/workflows/ci-pull-request.yml). Порядок проверок и правила зависимостей описаны в [документации Gradle](../code-quality/SKILL.md).

## Быстрый поиск из корня

```powershell
rg --files feature -g '*ViewModel.kt' -g '*Route.kt' -g '*EntryProviderInstaller.kt' -g '!**/build/**'
rg -n 'MainFlowEvent|MainRoute\.CharacterDetails' app core feature -g '*.kt' -g '!**/build/**'
rg -n 'MainRepository|@ContributesBinding' app feature -g '*.kt' -g '!**/build/**'
```
