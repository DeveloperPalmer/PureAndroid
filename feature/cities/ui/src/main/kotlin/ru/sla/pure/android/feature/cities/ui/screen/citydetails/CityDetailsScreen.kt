package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.uikit.theme.AppTheme

/**
 * Детали города. [CityDetailsViewModel] создаётся через свою assisted-фабрику: Hilt передаёт её
 * в `creationCallback`, а экран подставляет `cityId`. Для каждой записи стека — своя ViewModel:
 * ключ хранилища даёт декоратор ViewModelStore записи. Состояние экран читает через
 * `collectAsState()`, а одноразовые эффекты — через `collectSideEffect { }` из orbit-compose
 */
@Composable
fun CityDetailsScreen(
    cityId: Long,
    modifier: Modifier = Modifier,
    viewModel: CityDetailsViewModel = hiltViewModel<CityDetailsViewModel, CityDetailsViewModel.Factory>(
        creationCallback = { factory -> factory.create(cityId) },
    ),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    // Эффект, отправленный, пока экран не виден, Orbit придержит и отдаст, когда подписка вернётся
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SideEffect.ShowCityMessage -> {
                snackbarHostState.showSnackbar(
                    resources.getString(R.string.city_details_message, sideEffect.cityId)
                )
            }
        }
    }

    CityDetailsContent(
        cityId = state.cityId,
        snackbarHostState = snackbarHostState,
        onShowMessage = viewModel::onShowMessage,
        onBack = viewModel::onBack,
        modifier = modifier,
    )
}

/** UI деталей без ViewModel: его можно показать в превью и тестах */
@Composable
private fun CityDetailsContent(
    cityId: Long,
    snackbarHostState: SnackbarHostState,
    onShowMessage: () -> Unit,
    onBack: () -> Unit,
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
                text = stringResource(R.string.city_details_title, cityId),
                style = AppTheme.typography.headline,
            )
            Button(onClick = onShowMessage) {
                Text(stringResource(R.string.city_details_show_message))
            }
            Button(onClick = dropUnlessResumed(block = onBack)) {
                Text(stringResource(R.string.city_details_back))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.BottomCenter,
        ) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityDetailsContentPreview() {
    AppTheme {
        CityDetailsContent(
            cityId = 1L,
            snackbarHostState = remember { SnackbarHostState() },
            onShowMessage = {},
            onBack = {},
        )
    }
}
