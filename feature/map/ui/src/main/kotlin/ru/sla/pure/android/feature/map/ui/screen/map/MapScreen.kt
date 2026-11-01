package ru.sla.pure.android.feature.map.ui.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.uikit.theme.AppTheme

/** Заглушка карты. Сама карта (Yandex MapKit) сюда не подключается: проверяем только навигацию */
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
  // Surface даёт экрану фон и цвет текста из токенов темы: без него сквозь экран
  // видно окно Activity
  Surface(
    modifier = modifier.fillMaxSize(),
    color = AppTheme.colors.backgroundPrimary,
    contentColor = AppTheme.colors.contentPrimary,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stringResource(R.string.map_title),
        style = AppTheme.typography.headline,
      )
    }
  }
}
