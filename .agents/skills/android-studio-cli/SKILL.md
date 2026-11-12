---
name: android-studio-cli
description: Доступ к Android Studio для PureAndroid через Android CLI, проверка подключения и согласованный рендер Compose Preview.
---

# Android Studio через Android CLI

Android CLI установлен в окружении разработчика. Загрузчик находится в `%LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest\bin\android.exe`, сама установка — в `%USERPROFILE%\.android`. Каталог загрузчика добавлен в пользовательский `PATH`. Уже открытым терминалам и Codex может потребоваться перезапуск для обновления окружения.

Проверка версии и подключения из PowerShell.

```powershell
android --version
android studio check
```

Если текущий процесс ещё использует прежний `PATH`, вызови загрузчик напрямую.

```powershell
& "$env:LOCALAPPDATA/Android/Sdk/cmdline-tools/latest/bin/android.exe" studio check
```

Для PureAndroid указывай `--project=PureAndroid` и сверяй путь проекта в выводе `studio check`. Для задач с индексом или рендером требуется состояние `READY` и актуальная модель проекта в Studio. Синхронизацию, сборку и анализ запускай по [правилам проекта](../code-quality/SKILL.md).

Пример рендера существующего Preview из корня проекта. Его выполняют после согласования проверки по [чек-листу UI](../compose-ui-review/SKILL.md).

```powershell
$previewImage = Join-Path $env:TEMP 'PureAndroid-MainContentPreview.png'
android studio render-compose-preview --project=PureAndroid --print-semantics --output-image-file=$previewImage feature/main/ui/src/main/kotlin/ru/sla/pure/android/feature/main/ui/screen/main/MainScreenPreview.kt MainContentPreview
```

Сохраняй картинки во временную папку и проверяй, какая версия кода, тема и конфигурация Preview были отрисованы. Разделяй проверку подключения и проверку самого изображения.

[Обзор Android CLI](https://developer.android.com/tools/agents/android-cli) · [Рендер Compose Preview](https://developer.android.com/tools/agents/android-cli/commands/studio_render-compose-preview)
