package ru.sla.pure.android.core.routing

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey

data class NavigationTab(
  val route: NavKey,
  @param:DrawableRes
  val icon: Int,
  @param:StringRes
  val label: Int,
)
