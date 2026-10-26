package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey
import org.junit.Test

class FlowMetadataTest {

    @Test
    fun `запись флоу с корнем под ней проходит проверку`() {
        checkFlowRoot(partOfFlow(Root, Child), listOf(Other, Root, Child))
    }

    @Test(expected = IllegalStateException::class)
    fun `запись флоу без корня в стеке падает`() {
        checkFlowRoot(partOfFlow(Root, Child), listOf(Other, Child))
    }

    @Test(expected = IllegalStateException::class)
    fun `запись флоу с корнем над ней падает`() {
        checkFlowRoot(partOfFlow(Root, Child), listOf(Child, Root))
    }

    @Test
    fun `запись флоу, которой уже нет в стеке, проходит проверку`() {
        checkFlowRoot(partOfFlow(Root, Child), listOf(Other))
    }

    @Test
    fun `запись вне флоу проходит проверку`() {
        checkFlowRoot(emptyMap(), listOf(Other))
    }

    private data object Root : NavKey

    private data object Child : NavKey

    private data object Other : NavKey
}
