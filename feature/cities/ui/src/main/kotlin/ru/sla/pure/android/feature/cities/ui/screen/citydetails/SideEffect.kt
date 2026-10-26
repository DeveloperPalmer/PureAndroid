package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import androidx.compose.runtime.Immutable

/**
 * Одноразовые эффекты экрана деталей. В отличие от [ViewState], эффект не хранится: экран выполняет
 * его один раз и забывает — после поворота снэкбар не покажется снова
 */
@Immutable
sealed interface SideEffect {

    @Immutable
    data class ShowCityMessage(val cityId: Long) : SideEffect
}
