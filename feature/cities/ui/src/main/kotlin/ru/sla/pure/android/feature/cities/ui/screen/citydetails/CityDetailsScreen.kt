package ru.sla.pure.android.feature.cities.ui.screen.citydetails

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

/** Заглушка деталей города: показывает, какой id пришёл в ключе навигации */
@Composable
fun CityDetailsScreen(
    cityId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.city_details_title, cityId),
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onBack) {
            Text(stringResource(R.string.city_details_back))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityDetailsScreenPreview() {
    CityDetailsScreen(cityId = 1L, onBack = {})
}
