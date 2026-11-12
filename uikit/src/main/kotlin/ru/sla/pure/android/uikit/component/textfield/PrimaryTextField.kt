package ru.sla.pure.android.uikit.component.textfield

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.sla.pure.android.core.ui.text.TextRef
import ru.sla.pure.android.core.ui.text.resolveTextRef
import ru.sla.pure.android.core.ui.text.strRef
import ru.sla.pure.android.uikit.theme.AppTheme
import ru.sla.pure.android.uikit.theme.ColorTheme

// Условие внутри AnimatedContent поднять наружу нельзя: во время exit-анимации лямбда вызывается
// с прошлым состоянием, и именно поэтому текст ошибки успевает исчезнуть плавно. Поднятое условие
// сняло бы его мгновенно.
@Suppress("ConditionCouldBeLifted")
@Composable
fun PrimaryTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  errorText: TextRef? = null,
  placeholder: TextRef = strRef(""),
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  contentFadeKey: Any? = null,
  focusRequestKey: Any? = null
) {
  // Размер самого поля анимирует TextFieldDecoration внутри своего surface; здесь остаётся
  // только появление/скрытие текста ошибки — второй animateContentSize снаружи дал бы
  // двойную анимацию, догоняющую внутреннюю.
  Column(modifier = modifier) {
    TextFieldInternal(
      modifier = Modifier.fillMaxWidth(),
      value = value,
      onValueChange = onValueChange,
      shape = RoundedCornerShape(16.dp),
      enabled = enabled,
      isError = errorText != null,
      placeholder = placeholder,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      contentFadeKey = contentFadeKey,
      focusRequestKey = focusRequestKey,
      colors = TextFieldDefaults.primaryDefaultColors()
    )
    val fadeTween = AppTheme.motion.mediumTween<Float>()
    val sizeTween = AppTheme.motion.mediumTween<IntSize>()
    AnimatedContent(
      targetState = errorText,
      transitionSpec = {
        (fadeIn(fadeTween) + expandVertically(sizeTween))
          .togetherWith(fadeOut(fadeTween) + shrinkVertically(sizeTween))
      },
      label = "textFieldError"
    ) { targetErrorText ->
      if (targetErrorText != null) {
        Text(
          modifier = Modifier.padding(top = 6.dp, start = 12.dp),
          text = resolveTextRef(targetErrorText),
          style = AppTheme.typography.body3,
          color = AppTheme.colors.errorPrimary
        )
      }
    }
  }
}

@Preview
@Composable
private fun PrimaryTextFieldPreviewLight(
  @PreviewParameter(TextFieldPreviewStateProvider::class)
  state: TextFieldPreviewState
) {
  AppTheme(colorTheme = ColorTheme.Light) {
    PrimaryTextFieldPreviewContent(state)
  }
}

@Preview
@Composable
private fun PrimaryTextFieldPreviewDark(
  @PreviewParameter(TextFieldPreviewStateProvider::class)
  state: TextFieldPreviewState
) {
  AppTheme(colorTheme = ColorTheme.Dark) {
    PrimaryTextFieldPreviewContent(state)
  }
}

@Composable
private fun PrimaryTextFieldPreviewContent(state: TextFieldPreviewState) {
  Column(
    modifier = Modifier.background(AppTheme.colors.backgroundPrimary),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    PrimaryTextField(
      modifier = Modifier.fillMaxWidth(),
      value = state.text,
      onValueChange = {},
      enabled = state.enabled,
      errorText = if (state.isError) strRef("Ошибка валидации") else null
    )
  }
}
