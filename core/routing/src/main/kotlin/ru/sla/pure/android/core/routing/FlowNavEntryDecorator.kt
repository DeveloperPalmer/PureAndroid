package ru.sla.pure.android.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.ViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.cancel

/**
 * Даёт записям флоу DI-граф их флоу: внутри записи `metroViewModel()` создаёт ViewModel из этого
 * графа. Запись вне флоу декоратор показывает как есть.
 *
 * Граф лежит в хранилище флоу — хранилище корня флоу, которое запись находит по ключу флоу
 * ([flowContentKey]). При первом показе экрана флоу граф собирается и кладётся туда, дальше все
 * записи флоу получают этот же экземпляр. Сколько живёт граф:
 * - переживает поворот экрана;
 * - удаляется вместе с хранилищем корня, когда корень флоу уходит из стека или Activity
 *   завершается, и тогда же отменяется scope флоу.
 *
 * Работает в паре с `ViewModelStoreNavEntryDecorator` на том же [viewModelStoreProvider]: тот создаёт
 * хранилища записей и очищает их, когда запись уходит из стека, а этот только находит нужное. Пока
 * запись флоу на экране, она держит хранилище корня, и оно не очистится раньше неё. Корень флоу должен
 * лежать в [backStack] под его записями: иначе хранилище флоу некому очистить, поэтому без корня
 * декоратор падает с ошибкой ([checkFlowRoot]).
 */
class FlowNavEntryDecorator<T : Any>(
  viewModelStoreProvider: ViewModelStoreProvider,
  backStack: List<NavKey>,
) : NavEntryDecorator<T>(
    decorate = { entry ->
      val flow = flowEntry(entry.metadata)
      if (flow == null) {
        entry.Content()
      } else {
        checkFlowRoot(entry.metadata, backStack)
        FlowContent(entry, flow, viewModelStoreProvider)
      }
    },
  )

@Composable
fun <T : Any> rememberFlowNavEntryDecorator(
  backStack: List<NavKey>,
  viewModelStoreProvider: ViewModelStoreProvider,
): FlowNavEntryDecorator<T> {
  return remember(viewModelStoreProvider, backStack) {
    FlowNavEntryDecorator(
      viewModelStoreProvider = viewModelStoreProvider,
      backStack = backStack,
    )
  }
}

@Composable
private fun <T : Any> FlowContent(
  entry: NavEntry<T>,
  flow: FlowEntry,
  viewModelStoreProvider: ViewModelStoreProvider,
) {
  val flowKey = flowContentKey(flow.root)
  val holder = viewModel(
    key = flowKey,
    modelClass = FlowGraphHolder::class,
    factory = viewModelFactory { initializer { FlowGraphHolder(flow.component()) } },
    viewModelStoreOwner = rememberViewModelStoreOwner(
      key = flowKey,
      provider = viewModelStoreProvider
    ),
  )
  CompositionLocalProvider(LocalMetroViewModelFactory provides holder.viewModelFactory) {
    entry.Content()
  }
}

/**
 * Обёртка-ViewModel над графом флоу: через неё граф лежит в хранилище флоу и удаляется вместе
 * с ним, а при удалении отменяет scope флоу: корутины моделей флоу не переживают сам флоу.
 */
private class FlowGraphHolder(private val component: FlowComponent) : ViewModel() {
  // Фабрика ViewModel не скоуплена в графе: берём её один раз, а не при каждой рекомпозиции
  val viewModelFactory: MetroViewModelFactory = component.metroViewModelFactory

  override fun onCleared() {
    component.flowCoroutineScope().cancel()
  }
}
