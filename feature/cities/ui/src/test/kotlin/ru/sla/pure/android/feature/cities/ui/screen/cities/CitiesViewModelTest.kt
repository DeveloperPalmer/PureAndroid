package ru.sla.pure.android.feature.cities.ui.screen.cities

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import ru.sla.pure.android.feature.cities.ui.routing.RecordingFlowEventSink

class CitiesViewModelTest {

    private val eventSink = RecordingFlowEventSink()

    @Test
    fun `при создании показывает список городов`() = runTest {
        CitiesViewModel(eventSink).testWithInternalState(this) {
            runOnCreate()

            expectInternalState { copy(cityIds = listOf(1L, 2L, 3L)) }
        }
    }

    @Test
    fun `клик по городу просит роутинг открыть его детали`() = runTest {
        CitiesViewModel(eventSink).testWithInternalState(this) {
            containerHost.onCityClick(cityId = 2L).join()
        }

        assertEquals(listOf(FlowEvent.CityDetailsRequested(cityId = 2L)), eventSink.events)
    }
}
