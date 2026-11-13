package ru.sla.pure.android.app.routing

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

/**
 * Анимации переходов `NavDisplay`: горизонтальный сдвиг вместо стандартного затухания.
 * Predictive back рисует слои сцен отдельно от этих обычных переходов.
 */
internal object SlideTransitions {
  /** Вперёд: новый экран въезжает справа, текущий уходит влево */
  fun <T : Any> forward(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally { fw -> fw } togetherWith slideOutHorizontally { fw -> -fw }
  }

  /** Назад: предыдущий экран возвращается слева, текущий уходит вправо */
  fun <T : Any> back(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally { fw -> -fw } togetherWith slideOutHorizontally { fw -> fw }
  }

  /** Как у pager: направление определяется положением вкладок, включая сохранённые экраны их стеков. */
  fun <T : Any> AnimatedContentTransitionScope<Scene<T>>.betweenTabs(policy: TabNavigationPolicy): ContentTransform? {
    val direction = policy.direction(
      initialState.entries.last().contentKey,
      targetState.entries.last().contentKey,
    ) ?: return null
    return slideInHorizontally(
      animationSpec = tween(300)
    ) { it * direction } togetherWith slideOutHorizontally(
      animationSpec = tween(300)
    ) { -it * direction }
  }
}
