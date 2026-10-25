package ru.sla.pure.android.core.routing

import androidx.navigation3.runtime.NavKey

interface Navigator {

    fun navigateTo(route: NavKey)

    fun navigateBack()
}
