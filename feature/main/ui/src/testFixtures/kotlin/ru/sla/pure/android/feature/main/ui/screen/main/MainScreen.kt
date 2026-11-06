package ru.sla.pure.android.feature.main.ui.screen.main

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.kaspersky.components.composesupport.core.KNode
import io.github.kakaocup.compose.node.element.ComposeScreen
import ru.sla.pure.android.core.resources.R

/** Shared interactions for both isolated screen tests and app-level journeys. */
class MainScreen(
  semanticsProvider: SemanticsNodeInteractionsProvider,
) : ComposeScreen<MainScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag(MainScreenTestTags.SCREEN) },
  ) {
  val loadingIndicator: KNode = child {
    hasContentDescription(R.string.characters_loading)
  }

  val retryButton: KNode = child {
    hasText(R.string.characters_retry)
    hasClickAction()
  }

  val emptyMessage: KNode = child {
    hasText(R.string.characters_empty)
  }

  fun character(name: String): KNode = child {
    hasText(name)
  }

  fun error(
    @StringRes message: Int
  ): KNode = child {
    hasText(message)
  }
}
