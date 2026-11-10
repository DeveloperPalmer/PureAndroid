package ru.sla.pure.android.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Наблюдаемый результат запроса без последовательных повторов.
 *
 * Room выполняет запрос повторно при изменении таблицы, даже если результат остался прежним.
 * `distinctUntilChanged()` пропускает только изменившиеся результаты, поэтому запись другого
 * ключа в таблицу настроек не перезапускает наблюдение за текущей сессией.
 */
class DistinctFlow<T> internal constructor(upstream: Flow<T>) : Flow<T> by upstream.distinctUntilChanged()
