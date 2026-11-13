package ru.sla.pure.android.app.routing.predictive

/** Операции одной визуальной сессии; устаревший token не может изменить навигацию. */
internal interface BackPreviewPort {
  suspend fun start(sessionId: Long, context: BackContext): PreviewResult
  fun complete(sessionId: Long, context: BackContext): CommitResult

  /** Возвращается после применения reset display, чтобы следующий Back получил обычный slide. */
  suspend fun cancel(sessionId: Long)
}

internal enum class PreviewResult { Ready, Obsolete }
internal enum class CommitResult { Applied, Obsolete, AlreadyApplied }
