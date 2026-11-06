package ru.sla.pure.android.core.ui.lce

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import ru.kode.remo.JobFlow
import ru.kode.remo.JobState
import ru.sla.pure.android.core.ui.entity.ContentLoadState
import ru.sla.pure.android.core.ui.error.ErrorMapper
import ru.sla.pure.android.core.ui.error.baseErrorMapper

fun <T> JobFlow<T>.asLceState(
  errorMapper: ErrorMapper = ::baseErrorMapper,
  replayLastResult: Boolean = false
): Flow<ContentLoadState> {
  return channelFlow {
    launch {
      this@asLceState.results(replayLast = replayLastResult).collect {
        when (it) {
          is Ok -> send(ContentLoadState.Ready)
          is Err -> send(ContentLoadState.Error(errorMapper(it.error)))
        }
      }
    }
    launch {
      this@asLceState.state.collect {
        when (it) {
          JobState.Idle -> Unit // relying on result() emissions to report idle
          JobState.Running -> send(ContentLoadState.Loading)
        }
      }
    }
  }
}
