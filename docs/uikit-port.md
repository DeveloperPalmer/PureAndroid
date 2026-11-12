# Перенос UIKit из clarify-android

Источник: `D:/AndroidStudioProjects/clarify-android/uikit/src/main`.
Исходный проект не изменялся. Рабочее дерево PureAndroid перед переносом было чистым.

## Перенесено

- Вся internal ColorPalette, 56 цветовых ролей AppColors, LightColors/DarkColors с исходными alpha,
  включая graphLane1–6 и graphMergeContent.
- Все исходные стили AppTypography, Roboto regular/medium/bold, AppShapes, AppMotion и AppElevation.
- Семейства Primary/Secondary/Tertiary кнопок, варианты Small и Medium, иконки, текстовые кнопки,
  состояния disabled/loading, стили Default/Error/Success и исходные анимации.
- PrimaryTextField, SecondaryTextField и общая реализация TextField: String/TextFieldValue,
  ошибки, placeholder, фокус, клавиатура, изменение размера и анимации текста.
- TopAppBar, PrimaryTabsRow/Tab и общая реализация TabsRow, Avatar, IconAction, GoogleIcon,
  Divider, AppPager, RevealSplitRow, UnreadCountBadge, ModalBottomSheet, Popup/PopupScrim/ScrimEffect.
- Modifier, keyboard, pager extension, shared transition и preview utilities.
- ScreenScaffold для существующих Loading/Ready/Error. Ошибка отображает UiError.messageResource;
  экран может передать собственный слот ошибки с действием ViewModel. Существующие модели не менялись.
- Пять drawable, используемых компонентами и previews, и строка G. Три файла Roboto идентичны источнику.
  Сохранены исходные previews, добавлены обе темы для tabs и состояния scaffold.

## Интеграция

AppTheme сохраняет системный выбор темы и явный colorTheme. Все роли Material 3 ColorScheme,
типографика и Shapes связаны с перенесёнными токенами. LocalContentColor — contentPrimary:
cardPrimary из источника является фоном и не подходит для обычного текста.
Цвета выделения установлены внутри MaterialTheme, чтобы Material не заменял их своими значениями.

На экране входа локальные цвета/типографика и CredentialsButton заменены AppTheme,
PrimaryButton и SecondaryButton. Удалены дубликаты шрифтов login. Раскладка, тексты,
состояние загрузки и вызовы ViewModel сохранены; навигация и Orbit/FlowEventSink не менялись.
Существующие headline/title/body/bodySmall/label сохранены совместимыми стилями с прежними размерами
и Roboto; поэтому остальные экраны продолжают использовать свой API без редизайна.

Используется только существующий пакет uikit.spacer. WindowBars применяется к окну bottom sheet,
не меняет enableEdgeToEdge в MainActivity; контраст навигационной панели устанавливается с API 29,
цвет панели — до API 35. Проверка на устройстве ещё требуется.

## Зависимости и адаптации

- Сохранены android-library-convention и compose-convention, существующие core:ui/core:resources.
- Добавлены implementation coil-compose и coil-network-okhttp 3.6.3 через каталог версий.
  В PureAndroid не было загрузчика изображений. Используется coil3.compose.AsyncImage;
  сетевой модуль нужен для photoUrl. Версия и разделение модулей взяты из официальной документации:
  https://coil-kt.github.io/coil/getting_started/ и https://coil-kt.github.io/coil/upgrading_to_coil3/.
- TextRef адаптирован небольшим типом core.ui.text: literal/ресурс строки с аргументами,
  разрешение через stringResource в композиции. Внешняя библиотека resourcerefs не добавляется.
- Placeholder универсальных полей по умолчанию пустой, задаётся вызывающим кодом.
- Entity Clarify, Arrow Optics/KSP и activity-compose не нужны выбранным реализациям.
- Popup открыт как публичный компонент. В TabsRow исправлены выбор отсутствующей вкладки
  и устаревающий индекс при изменении списка. Avatar учитывает modifier и для изображения.

## Исключено и уточнённый объём

Не переносились component/chat/**, ChatTextField, ChatScrollToBottomButton, MessageQuoteColors,
доменные модели, API, feature UI чатов/веток/merge requests/хронологии и их ресурсы.
Графовые цветовые токены при этом сохранены полностью.

После уточнения разработчика «Ничего нового придумывать не нужно» новая инфраструктура ViewEvent,
mediator и host states не вводилась. Исходные ViewEventsHost/ViewEventComponents, NotStarted,
refreshInProgress и действия в UiError не копировались: используются модели и обработчики PureAndroid.
Если этот отдельный слой впоследствии понадобится, потребуется отдельное решение по его интеграции.
Других открытых вопросов для уточнённого объёма нет.

## Проверки

Выполнена статическая сверка: точное совпадение ColorPalette/AppColors/LightColors/DarkColors после
замены пакета, 56 ролей, побайтовое совпадение шрифтов, существование drawable/font,
разбор XML и TOML, проверка обращений к AppTheme и локальных импортов UIKit,
отсутствие ссылок на ru.sla.clarify/resourcerefs/Arrow и исключённый placeholder.
`git diff --check` прошёл. Это не подтверждение компиляции.

Gradle, синхронизация, форматтеры, Detekt, тесты и рендер previews агентом не запускались.
Новых тестов нет. Разрешённые compile/runtime графы зависимостей не снимались,
поэтому итоговые транзитивные версии Coil/OkHttp/Compose ещё требуют проверки.
Коммиты и PR не создавались.

Команды для разработчика из корня PureAndroid:

```powershell
.\gradlew.bat :uikit:compileDebugKotlin :core:ui:compileDebugKotlin :feature:login:ui:compileDebugKotlin :feature:main:ui:compileDebugKotlin :feature:map:ui:compileDebugKotlin :app:assembleDebug
.\gradlew.bat spotlessCheck
.\gradlew.bat :uikit:detektDebug :feature:login:ui:detektDebug
.\gradlew.bat :uikit:dependencies --configuration debugCompileClasspath
.\gradlew.bat :uikit:dependencies --configuration releaseRuntimeClasspath
.\gradlew.bat :app:dependencies --configuration debugRuntimeClasspath
.\gradlew.bat :app:dependencies --configuration releaseRuntimeClasspath
.\gradlew.bat :app:dependencyInsight --configuration releaseRuntimeClasspath --dependency coil
.\gradlew.bat :app:dependencyInsight --configuration releaseRuntimeClasspath --dependency okhttp
.\gradlew.bat :app:dependencyInsight --configuration releaseRuntimeClasspath --dependency androidx.compose
```

После сборки: открыть светлые/тёмные previews кнопок и полей с параметрами состояний,
проверить вход Google/email и блокировку повторного клика при загрузке, список/детали/профиль,
вкладки и возврат. На устройстве проверить bottom sheet с IME, жестовую/трёхкнопочную навигацию,
API 26–28, 29–34 и 35+, масштаб шрифта и TalkBack. Эти проверки пока не выполнялись.
