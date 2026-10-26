package ru.sla.pure.android.feature.cities.ui.screen.cities

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.feature.cities.domain.CitiesModel
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import ru.sla.pure.android.feature.cities.ui.routing.RecordingFlowEventSink

class CitiesViewModelTest {

    private val eventSink = RecordingFlowEventSink()

    @Test
    fun `показывает города из модели флоу`() = runTest {
        CitiesViewModel(CitiesModel(), eventSink).testWithInternalState(this) {
            // Подписка на модель не завершается сама: без отмены тест ждал бы её до таймаута
            val subscription = runOnCreate()

            expectInternalState { copy(cityIds = listOf(1L, 2L, 3L)) }
            subscription.cancel()
        }
    }

    @Test
    fun `клик по городу просит роутинг открыть его детали`() = runTest {
        CitiesViewModel(CitiesModel(), eventSink).testWithInternalState(this) {
            containerHost.onCityClick(cityId = 2L).join()
        }

        assertEquals(listOf(FlowEvent.CityDetailsRequested(cityId = 2L)), eventSink.events)
    }
}
