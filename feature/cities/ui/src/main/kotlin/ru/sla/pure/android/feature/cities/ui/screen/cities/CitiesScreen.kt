package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import ru.sla.pure.android.feature.cities.ui.R

/** Заглушка списка городов: вместо данных — кнопки, которые открывают детали города */
@Composable
fun CitiesScreen(
    onCityClick: (cityId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Surface даёт экрану фон и цвет текста темы: без него сквозь экран видно окно Activity
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.cities_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            STUB_CITY_IDS.forEach { cityId ->
                // dropUnlessResumed отбрасывает клик, пока экран не RESUMED, то есть пока идёт
                // анимация перехода: иначе двойной тап положит в стек два экрана деталей
                Button(onClick = dropUnlessResumed { onCityClick(cityId) }) {
                    Text(stringResource(R.string.cities_city, cityId))
                }
            }
        }
    }
}

private val STUB_CITY_IDS = listOf(1L, 2L, 3L)

@Preview(showBackground = true)
@Composable
private fun CitiesScreenPreview() {
    CitiesScreen(onCityClick = {})
}
