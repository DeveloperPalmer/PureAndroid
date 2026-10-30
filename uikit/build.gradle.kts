plugins {
  id("compose-convention")
}

// Дизайн-система: тема и общие Compose-компоненты. Ресурсы и базовый UI-слой приходят через
// core:ui из compose-convention, поэтому своих зависимостей у модуля пока нет
android {
  namespace = "ru.sla.pure.android.uikit"
}
