package ru.sla.pure.android.feature.main.domain.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.sla.pure.android.core.domain.entity.ApiError
import ru.sla.pure.android.core.domain.entity.ConnectivityError
import ru.sla.pure.android.feature.main.domain.MainRepository
import ru.sla.pure.android.feature.main.domain.PAGE_SIZE
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

class CharacterPagingSourceTest {
  private val repository = mockk<MainRepository>()
  private val source = CharacterPagingSource(repository)

  @Test
  @DisplayName("Отмена загрузки не превращается в ошибку Paging")
  fun `load cancellation is propagated`() = runTest {
    // Paging ждёт ошибки значением (LoadResult.Error), а отмену корутины — исключением
    coEvery { repository.readPage(1) } throws CancellationException("scope cancelled")

    assertThrows<CancellationException> {
      val params = PagingSource.LoadParams.Refresh<Int>(
        key = null,
        loadSize = PAGE_SIZE,
        placeholdersEnabled = false
      )
      source.load(params)
    }
  }

  @Test
  @DisplayName("Ошибка репозитория возвращается как LoadResult.Error")
  fun `repository failure returns a load error`() = runTest {
    val errors = listOf(
      ConnectivityError.NoConnection(null),
      ApiError(500, null, "Internal server error"),
      IllegalStateException("unexpected failure"),
    )
    val params = PagingSource.LoadParams.Refresh<Int>(
      key = null,
      loadSize = PAGE_SIZE,
      placeholdersEnabled = false
    )

    for (error in errors) {
      coEvery { repository.readPage(1) } throws error
      val result = source.load(params)

      assertTrue(result is PagingSource.LoadResult.Error)
      assertSame(error, (result as PagingSource.LoadResult.Error).throwable)
    }
  }

  @Test
  @DisplayName("Обновление восстанавливает страницу с текущей позицией прокрутки")
  fun `refresh key restores the page containing the anchor`() {
    // Пользователь смотрит вторую страницу (элемент 25) → refresh должен начаться со страницы 2,
    // иначе после обновления список перепрыгнет на начало
    val state = PagingState(
      config = PagingConfig(
        pageSize = PAGE_SIZE
      ),
      pages = listOf(
        PagingSource.LoadResult.Page(
          data = characters(1..PAGE_SIZE),
          prevKey = null,
          nextKey = 2
        ),
        PagingSource.LoadResult.Page(
          data = characters(21..40),
          prevKey = 1,
          nextKey = 3
        ),
      ),
      anchorPosition = 25,
      leadingPlaceholderCount = 0,
    )

    assertEquals(2, source.getRefreshKey(state))
  }

  @Test
  @DisplayName("Обновление без позиции прокрутки начинается с первой страницы")
  fun `refresh without an anchor starts from the first page`() {
    val state = PagingState<Int, Character>(
      config = PagingConfig(
        pageSize = PAGE_SIZE
      ),
      pages = emptyList(),
      anchorPosition = null,
      leadingPlaceholderCount = 0,
    )

    assertNull(source.getRefreshKey(state))
  }

  private fun characters(range: IntRange): List<Character> = range.map(::character)

  private fun character(id: Int) = Character(
    id,
    "Character $id",
    CharacterStatus.Alive,
    "Human",
    "",
    CharacterGender.Male,
    CharacterLocation("Earth", ""),
    CharacterLocation("Citadel", ""),
    "",
  )
}
