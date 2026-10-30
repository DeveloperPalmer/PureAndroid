# Форматирование и статический анализ

Настройки перенесены из `kmp-base-project`: ktlint запускается через Spotless,
а Detekt подключается к Android- и JVM-модулям общей `detekt-convention`.

```powershell
.\gradlew.bat spotlessApply   # применить форматирование Kotlin и Gradle Kotlin DSL
.\gradlew.bat spotlessCheck   # проверить форматирование без изменения файлов
.\gradlew.bat prePushCheck    # форматирование, затем Detekt
```

`prePushCheck` запускает `detektDebug` в Android-модулях и `detektMain`
в JVM-модулях. Анализ использует разрешение типов и JVM target 11.
Тесты запускаются отдельно; эта задача не устанавливает Git hook.

Правила форматирования находятся в `.editorconfig`, правила анализа — в
`detekt-config.yml`, версии инструментов — в `gradle/libs.versions.toml`.
Сгенерированные исходники исключены из анализа, каталоги сборки — из форматирования.
Для Kotlin и Gradle Kotlin DSL используются два пробела и окончания строк LF;
`.gitattributes` сохраняет их при работе с Git на Windows.
