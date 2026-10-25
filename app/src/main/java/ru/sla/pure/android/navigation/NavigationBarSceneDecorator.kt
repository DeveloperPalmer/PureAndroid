package ru.sla.pure.android.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.sla.pure.android.core.routing.NavigationBarKey
import ru.sla.pure.android.core.routing.showNavigationBar

/**
 * Добавляет нижнюю панель к сценам. Navigation 3 показывает записи стека в сценах (`Scene`),
 * а декоратор сцен оборачивает сцену общим UI. Панель получают только записи с [showNavigationBar].
 * Панель — часть сцены, а не слой поверх `NavDisplay`: при переходе к записи без панели сцена
 * со списком уходит вместе с панелью, а новый экран приходит на весь экран.
 *
 * Схема из рецепта Responsive Navigation Scene Decorator в android/nav3-recipes.
 */
class NavigationBarSceneDecoratorStrategy<T : Any>(
    private val sharedTransitionScope: SharedTransitionScope,
    private val navigationBar: @Composable () -> Unit,
) : SceneDecoratorStrategy<T> {

    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return if (scene.entries.all { it.metadata[NavigationBarKey] == true }) {
            NavigationBarScene(scene, sharedTransitionScope, navigationBar)
        } else {
            scene
        }
    }
}

/**
 * Стратегию помним через `remember`: Navigation 3 сравнивает сцены по equals, а сцена хранит
 * лямбду панели. Стабильная обёртка над [navigationBar] даёт одинаковые сцены между рекомпозициями
 */
@Composable
fun <T : Any> rememberNavigationBarSceneDecoratorStrategy(
    sharedTransitionScope: SharedTransitionScope,
    navigationBar: @Composable () -> Unit,
): NavigationBarSceneDecoratorStrategy<T> {
    val currentNavigationBar by rememberUpdatedState(navigationBar)
    return remember(sharedTransitionScope) {
        NavigationBarSceneDecoratorStrategy(sharedTransitionScope) { currentNavigationBar() }
    }
}

/** Сцена «контент + панель снизу». Data class: Navigation 3 сравнивает сцены по equals */
private data class NavigationBarScene<T : Any>(
    private val scene: Scene<T>,
    private val sharedTransitionScope: SharedTransitionScope,
    private val navigationBar: @Composable () -> Unit,
) : Scene<T> by scene {

    override val key: Any = scene::class to scene.key

    override val content: @Composable () -> Unit = {
        with(sharedTransitionScope) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    // Низ экрана закрывает панель, поэтому экранам сцены нижний отступ не нужен
                    modifier = Modifier
                        .weight(1f)
                        .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
                ) {
                    scene.content()
                }
                // У каждой сцены своя панель, все с одним ключом общего элемента. Между двумя
                // сценами с панелью Compose рисует её одну в общем месте, и она стоит на месте.
                // Если у следующей сцены панели нет, пары нет, и панель уходит вместе со своей сценой
                Box(
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(NAVIGATION_BAR_KEY),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    ),
                ) {
                    navigationBar()
                }
            }
        }
    }
}

private const val NAVIGATION_BAR_KEY = "navigation-bar"
