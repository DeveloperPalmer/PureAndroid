package ru.sla.pure.android.feature.map.ui.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = stringResource(R.string.map_title),
      style = AppTheme.typography.headline,
    )
  }
}
