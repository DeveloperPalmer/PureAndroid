package ru.sla.pure.android.core.ui.text

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource

/** Literal or localized text, resolved in composition so locale changes are observed. */
@Immutable
sealed interface TextRef {
  data class Literal(val text: String) : TextRef
  data class Resource(
    @param:StringRes val id: Int,
    val arguments: List<Any> = emptyList()
  ) : TextRef
}

fun strRef(text: String): TextRef = TextRef.Literal(text)

fun resRef(
  @StringRes id: Int,
  vararg arguments: Any
): TextRef = TextRef.Resource(id, arguments.toList())

@Composable
fun resolveTextRef(source: TextRef): String = when (source) {
  is TextRef.Literal -> source.text
  is TextRef.Resource -> if (source.arguments.isEmpty()) {
    stringResource(source.id)
  } else {
    // stringResource requires varargs; forwarding format arguments requires the spread operator.
    @Suppress("SpreadOperator")
    stringResource(source.id, *source.arguments.toTypedArray())
  }
}
