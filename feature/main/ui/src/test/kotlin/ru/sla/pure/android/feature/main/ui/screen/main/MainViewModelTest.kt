package ru.sla.pure.android.feature.main.ui.screen.main

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.feature.main.domain.MainModel

class MainViewModelTest {

    @Test
    fun `показывает города из модели флоу`() = runTest {
        MainViewModel(MainModel()).testWithInternalState(this) {
            // Подписка на модель не завершается сама: без отмены тест ждал бы её до таймаута
            val subscription = runOnCreate()

            expectInternalState { copy(cityIds = listOf(1L, 2L, 3L)) }
            subscription.cancel()
        }
    }
}
