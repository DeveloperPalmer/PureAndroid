plugins {
  id("android-library-convention")
}

// Общие ресурсы приложения: строки и иконки. Кода в модуле нет — только res. Модули берут ресурсы
// отсюда как ru.sla.pure.android.core.resources.R, а не держат свои
android {
  namespace = "ru.sla.pure.android.core.resources"
}
