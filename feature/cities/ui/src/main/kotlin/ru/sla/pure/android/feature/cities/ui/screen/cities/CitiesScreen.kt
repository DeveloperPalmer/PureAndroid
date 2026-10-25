package ru.sla.pure.android.feature.cities.ui.screen.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sla.pure.android.feature.cities.ui.R

/** Заглушка списка городов: вместо данных — кнопки, которые открывают детали города */
@Composable
fun CitiesScreen(
    onCityClick: (cityId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.cities_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        STUB_CITY_IDS.forEach { cityId ->
            Button(onClick = { onCityClick(cityId) }) {
                Text(stringResource(R.string.cities_city, cityId))
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
