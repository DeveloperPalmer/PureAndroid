package ru.sla.pure.android.uikit.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.sla.pure.android.core.resources.R
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.entity.UiError
import ru.sla.pure.android.uikit.theme.AppTheme
import ru.sla.pure.android.uikit.theme.ColorTheme

/** Presentation only: screen-specific retry actions belong in the contentLoadError slot. */
@Composable
fun ScreenScaffold(
  state: ScreenScaffoldState = rememberScreenScaffoldState(),
  modifier: Modifier = Modifier,
  contentLoadPlaceholder: @Composable () -> Unit = { ScreenScaffold.ContentLoadProgressIndicator() },
  contentLoadError: @Composable (ContentLoadState.Error) -> Unit = { ScreenScaffold.ContentLoadError(it) },
  contentReady: @Composable () -> Unit,
) {
  Box(modifier.fillMaxSize().background(AppTheme.colors.backgroundPrimary)) {
    when (val loadState = state.contentLoadState) {
      ContentLoadState.Loading -> contentLoadPlaceholder()
      ContentLoadState.Ready -> contentReady()
      is ContentLoadState.Error -> contentLoadError(loadState)
    }
  }
}

object ScreenScaffold {
  @Composable
  fun ContentLoadProgressIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  }

  @Composable
  fun ContentLoadError(errorState: ContentLoadState.Error) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(errorState.error.messageResource),
        style = AppTheme.typography.headline2,
        color = AppTheme.colors.contentPrimary,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Stable
class ScreenScaffoldState(contentLoadState: ContentLoadState = ContentLoadState.Ready) {
  var contentLoadState: ContentLoadState by mutableStateOf(contentLoadState)
}

@Composable
fun rememberScreenScaffoldState(
  contentLoadState: ContentLoadState = ContentLoadState.Ready,
): ScreenScaffoldState {
  val state = remember { ScreenScaffoldState(contentLoadState) }
  SideEffect { state.contentLoadState = contentLoadState }
  return state
}

internal class ScaffoldPreviewStates : PreviewParameterProvider<ContentLoadState> {
  override val values = sequenceOf(
    ContentLoadState.Loading,
    ContentLoadState.Ready,
    ContentLoadState.Error(UiError(R.string.error_connection)),
  )
}

@Preview
@Composable
private fun ScaffoldLight(
  @PreviewParameter(ScaffoldPreviewStates::class) state: ContentLoadState
) {
  ScaffoldPreview(state, ColorTheme.Light)
}

@Preview
@Composable
private fun ScaffoldDark(
  @PreviewParameter(ScaffoldPreviewStates::class) state: ContentLoadState
) {
  ScaffoldPreview(state, ColorTheme.Dark)
}

@Composable
private fun ScaffoldPreview(state: ContentLoadState, theme: ColorTheme) {
  AppTheme(colorTheme = theme) {
    ScreenScaffold(state = rememberScreenScaffoldState(state)) {
      Text(text = "Content", style = AppTheme.typography.body1)
    }
  }
}
