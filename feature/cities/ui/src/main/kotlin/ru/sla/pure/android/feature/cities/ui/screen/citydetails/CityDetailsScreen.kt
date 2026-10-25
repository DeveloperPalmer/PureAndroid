package ru.sla.pure.android.feature.cities.ui.screen.citydetails

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import ru.sla.pure.android.feature.cities.ui.R

/**
 * Детали города. [CityDetailsViewModel] создаётся через свою assisted-фабрику: Hilt передаёт её
 * в `creationCallback`, а экран подставляет `cityId`. Для каждой записи стека — своя ViewModel:
 * ключ хранилища даёт декоратор ViewModelStore записи
 */
@Composable
fun CityDetailsScreen(
    cityId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityDetailsViewModel =
        hiltViewModel<CityDetailsViewModel, CityDetailsViewModel.Factory>(
            creationCallback = { factory -> factory.create(cityId) },
        ),
) {
    CityDetailsContent(
        cityId = viewModel.cityId,
        onBack = onBack,
        modifier = modifier,
    )
}

/** UI деталей без ViewModel: его можно показать в превью и тестах */
@Composable
private fun CityDetailsContent(
    cityId: Long,
    onBack: () -> Unit,
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
                text = stringResource(R.string.city_details_title, cityId),
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(onClick = dropUnlessResumed(block = onBack)) {
                Text(stringResource(R.string.city_details_back))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityDetailsContentPreview() {
    CityDetailsContent(cityId = 1L, onBack = {})
}
