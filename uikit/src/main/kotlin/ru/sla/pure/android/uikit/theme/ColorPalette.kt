package ru.sla.pure.android.uikit.theme

import androidx.compose.ui.graphics.Color

// Палитра — сырые цвета дизайн-системы. Только internal: экраны и компоненты берут цвета по ролям
// из AppColors, а не по оттенкам. Нужного цвета нет среди ролей — значит, нужна новая роль
// в AppColors, а не прямой цвет из палитры
internal object ColorPalette {
  val gray0 = Color(0xFFFFFFFF)
  val gray50 = Color(0xFFF6F7F8)
  val gray100 = Color(0xFFECEEF0)
  val gray200 = Color(0xFFD9DDE1)
  val gray400 = Color(0xFF9AA3AB)
  val gray600 = Color(0xFF5D666E)
  val gray800 = Color(0xFF2A3035)
  val gray850 = Color(0xFF1F2428)
  val gray900 = Color(0xFF16191C)
  val gray950 = Color(0xFF0F1113)

  val teal100 = Color(0xFFD5F2EF)
  val teal300 = Color(0xFF4FD1C5)
  val teal500 = Color(0xFF0F8B80)
  val teal900 = Color(0xFF0E3A36)

  val red300 = Color(0xFFF2867A)
  val red600 = Color(0xFFC62828)

  val green300 = Color(0xFF7FD49A)
  val green600 = Color(0xFF2E7D32)
}
