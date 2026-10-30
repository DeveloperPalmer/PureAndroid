# Форматирование и статический анализ

Настройки перенесены из `kmp-base-project`: ktlint запускается через Spotless,
а Detekt подключается к Android- и JVM-модулям общей `detekt-convention`.

```powershell
.\gradlew.bat spotlessApply   # применить форматирование Kotlin и Gradle Kotlin DSL
.\gradlew.bat spotlessCheck   # проверить форматирование без изменения файлов
.\gradlew.bat prePushCheck    # форматирование, Detekt и анализ зависимостей
.\gradlew.bat buildHealth     # только анализ зависимостей
```

`prePushCheck` запускает `detektDebug` в Android-модулях и `detektMain`
в JVM-модулях. Анализ использует разрешение типов и JVM target 11.
Тесты запускаются отдельно; эта задача не устанавливает Git hook.

## Анализ зависимостей

Dependency Analysis Gradle Plugin применяется ко всем модулям основной сборки.
`buildHealth` выводит рекомендации в консоль и сохраняет сводные отчёты:

- `build/reports/dependency-analysis/build-health-report.txt`;
- `build/reports/dependency-analysis/build-health-report.json`.

Сейчас рекомендации имеют уровень `warn`: они требуют ревью и не блокируют
`prePushCheck`. Ошибки выполнения анализатора останавливают задачу. Автоматическое
изменение зависимостей в `prePushCheck` не включено.

Для разбора отдельного модуля и обоснования конкретного совета:

```powershell
.\gradlew.bat :app:data:projectHealth
.\gradlew.bat :app:data:reason --id :core:data
```

При ревью нужно учитывать публичные типы, сгенерированный код Metro/Room,
намеренный реэкспорт зависимостей и общие тестовые bundles. Рекомендации по
отдельным элементам bundle проверяются с учётом всех использующих его модулей.

Версия плагина находится в каталоге версий. Версия 3.20.0 предупреждает, что её
официально проверенный диапазон AGP заканчивается на 9.3.1; в проекте используется
AGP 9.4.1. После обновления плагина или AGP следует повторить полный `prePushCheck`.

Правила форматирования находятся в `.editorconfig`, правила анализа — в
`detekt-config.yml`, версии инструментов — в `gradle/libs.versions.toml`.
Сгенерированные исходники исключены из анализа, каталоги сборки — из форматирования.
Для Kotlin и Gradle Kotlin DSL используются два пробела и окончания строк LF;
`.gitattributes` сохраняет их при работе с Git на Windows.
