package ru.sla.pure.android.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

/**
 * Анимации переходов `NavDisplay`: горизонтальный сдвиг вместо стандартного затухания.
 * `NavDisplay` сам выбирает, какую применить: переход вперёд, назад или назад жестом
 * (predictive back — анимация идёт вслед за пальцем)
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

    /** Назад жестом: та же анимация, что и [back], но её прогресс задаёт жест */
    fun <T : Any> predictiveBack(): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
        slideInHorizontally { fw -> -fw } togetherWith slideOutHorizontally { fw -> fw }
    }
}
