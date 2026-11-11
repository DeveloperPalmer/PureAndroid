package ru.sla.pure.android.feature.app.ui.screen.initialresolve

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.orbitmvi.orbit.test.testWithInternalState
import ru.sla.pure.android.auth.session.domain.AuthSessionModel
import ru.sla.pure.android.auth.session.domain.entity.AuthSessionState
import ru.sla.pure.android.core.ui.Event
import ru.sla.pure.android.feature.app.ui.routing.FlowEvent
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class InitialResolveViewModelTest {
  private val dispatcher = StandardTestDispatcher()
  private val model = mockk<AuthSessionModel>()

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun activeSessionEmitsActiveEventOnce() = runTest(dispatcher) {
    every { model.sessionState } returns flowOf(AuthSessionState.Active, AuthSessionState.Inactive)
    val events = mutableListOf<Event>()

    InitialResolveViewModel(model) { events.add(it) }.testWithInternalState(this) {
      runOnCreate().join()
      cancelAndIgnoreRemainingItems()
    }
    assertEquals(listOf(FlowEvent.ActiveSessionResolved), events)
  }

  @Test
  fun inactiveSessionEmitsInactiveEvent() = runTest(dispatcher) {
    every { model.sessionState } returns flowOf(AuthSessionState.Inactive)
    val events = mutableListOf<Event>()

    InitialResolveViewModel(model) { events.add(it) }.testWithInternalState(this) {
      runOnCreate().join()
      cancelAndIgnoreRemainingItems()
    }
    assertEquals(listOf(FlowEvent.InactiveSessionResolved), events)
  }

  @Test
  fun storageFailureEmitsInactiveEvent() = runTest(dispatcher) {
    every { model.sessionState } returns flow { throw IOException("unavailable") }
    val events = mutableListOf<Event>()

    InitialResolveViewModel(model) { events.add(it) }.testWithInternalState(this) {
      runOnCreate().join()
      cancelAndIgnoreRemainingItems()
    }
    assertEquals(listOf(FlowEvent.InactiveSessionResolved), events)
  }
}
