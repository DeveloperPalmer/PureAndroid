package ru.sla.pure.android.feature.app.ui.screen.initialresolve

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun InitialResolveScreen(viewModel: InitialResolveViewModel) {
  viewModel.collectAsState()
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}
