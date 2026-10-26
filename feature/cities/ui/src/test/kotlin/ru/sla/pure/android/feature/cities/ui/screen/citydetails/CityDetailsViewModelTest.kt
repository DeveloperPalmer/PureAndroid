package ru.sla.pure.android.feature.cities.ui.screen.citydetails

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.orbitmvi.orbit.test.TestSettings
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.feature.cities.ui.routing.FlowEvent
import ru.sla.pure.android.feature.cities.ui.routing.RecordingFlowEventSink

class CityDetailsViewModelTest {

    private val eventSink = RecordingFlowEventSink()

    @Test
    fun `начальное состояние содержит город из ключа навигации`() = runTest {
        // Обычно начальное состояние Orbit проверяет сам; здесь оно и есть предмет теста, поэтому явно
        val settings = TestSettings(autoCheckInitialState = false)

        CityDetailsViewModel(cityId = 7L, eventSink = eventSink).testWithInternalState(this, settings = settings) {
            expectInternalState(ViewState(cityId = 7L))
        }
    }

    @Test
    fun `кнопка сообщения шлёт экрану эффект с городом`() = runTest {
        CityDetailsViewModel(cityId = 7L, eventSink = eventSink).testWithInternalState(this) {
            containerHost.onShowMessage()

            expectSideEffect(SideEffect.ShowCityMessage(cityId = 7L))
        }
    }

    @Test
    fun `назад просит роутинг закрыть детали`() = runTest {
        CityDetailsViewModel(cityId = 7L, eventSink = eventSink).testWithInternalState(this) {
            containerHost.onBack().join()
        }

        assertEquals(listOf(FlowEvent.CityDetailsDismissed), eventSink.events)
    }
}
