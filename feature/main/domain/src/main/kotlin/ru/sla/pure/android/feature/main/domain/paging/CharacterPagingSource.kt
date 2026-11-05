package ru.sla.pure.android.feature.main.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.feature.main.domain.entity.Character

internal class CharacterPagingSource(
  private val repository: CharacterRepository
) : PagingSource<Int, Character>() {
  // Paging receives load failures as values; coroutine cancellation must still propagate.
  @Suppress("TooGenericExceptionCaught")
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
    return try {
      val page = repository.readPage(params.key ?: 1)
      LoadResult.Page(
        data = page.characters,
        prevKey = page.previousPage,
        nextKey = page.nextPage
      )
    } catch (error: CancellationException) {
      throw error
    } catch (error: ApiError) {
      LoadResult.Error(error)
    } catch (error: ConnectivityError) {
      LoadResult.Error(error)
    } catch (error: Exception) {
      LoadResult.Error(error)
    }
  }

  override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
    val anchor = state.anchorPosition ?: return null
    val page = state.closestPageToPosition(anchor) ?: return null
    return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
  }
}
