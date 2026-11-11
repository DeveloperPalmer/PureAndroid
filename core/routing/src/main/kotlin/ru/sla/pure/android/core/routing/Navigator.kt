package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey

/**
 * Переходы между экранами. Каждый экземпляр управляет навигацией своего флоу.
 */
interface Navigator {
  /** Задаёт стартовый экран при входе во флоу, до создания навигационного стека. */
  fun setRoot(route: NavKey)

  /**
   * Открыть экран [route]:
   * - если это ключ вкладки, переключается вкладка;
   * - иначе экран добавляется в стек текущей вкладки.
   */
  fun navigateTo(route: NavKey)

  /** Шаг назад — то же, что системная кнопка «Назад» */
  fun navigateBack()

  /** Заменяет стек контейнера без вкладок одним экраном после запуска приложения. */
  fun replaceRoot(route: NavKey)
}
