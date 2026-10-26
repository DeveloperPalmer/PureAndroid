package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import org.orbitmvi.orbit.compose.collectAsState
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.uikit.theme.AppTheme

/**
 * Список городов. ViewModel по умолчанию берётся из Hilt: `hiltViewModel()` находит хранилище
 * ViewModel текущей записи стека и создаёт [CitiesViewModel] через фабрику Hilt.
 * `collectAsState()` из orbit-compose подписывается на состояние контейнера с учётом жизненного
 * цикла: пока экран не виден, подписка остановлена
 */
@Composable
fun CitiesScreen(
    modifier: Modifier = Modifier,
    viewModel: CitiesViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    CitiesContent(
        cityIds = state.cityIds,
        onCityClick = viewModel::onCityClick,
        modifier = modifier,
    )
}

/** UI списка без ViewModel: его можно показать в превью и тестах */
@Composable
private fun CitiesContent(
    cityIds: List<Long>,
    onCityClick: (cityId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Surface даёт экрану фон и цвет текста из токенов темы: без него сквозь экран
    // видно окно Activity
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppTheme.colors.backgroundPrimary,
        contentColor = AppTheme.colors.contentPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.cities_title),
                style = AppTheme.typography.headline,
            )
            cityIds.forEach { cityId ->
                // dropUnlessResumed отбрасывает клик, пока экран не RESUMED, то есть пока идёт
                // анимация перехода: иначе двойной тап положит в стек два экрана деталей
                Button(onClick = dropUnlessResumed { onCityClick(cityId) }) {
                    Text(stringResource(R.string.cities_city, cityId))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CitiesContentPreview() {
    AppTheme {
        CitiesContent(cityIds = listOf(1L, 2L, 3L), onCityClick = {})
    }
}
