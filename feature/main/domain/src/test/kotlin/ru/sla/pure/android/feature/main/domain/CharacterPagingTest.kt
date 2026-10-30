package ru.sla.pure.android.feature.main.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.feature.main.domain.paging.CharacterPagingSource

class CharacterPagingTest {
  @Test
  fun `страницы сохраняют серверные ключи независимо от loadSize`() = runTest {
    val repo = Repository()
    val source = CharacterPagingSource(repo)
    val result = source.load(PagingSource.LoadParams.Refresh(2, 60, false)) as PagingSource.LoadResult.Page
    assertEquals((21..40).toList(), result.data.map { it.id })
    assertEquals(1, result.prevKey)
    assertNull(result.nextKey)
    assertEquals(listOf(2), repo.calls)
  }

  @Test
  fun `ошибка репозитория становится LoadResult Error`() = runTest {
    val repo = Repository()
    val failure = ServerException.Unreachable(null)
    repo.failure = failure
    val result = CharacterPagingSource(repo).load(PagingSource.LoadParams.Refresh(null, 20, false))
    assertSame(failure, (result as PagingSource.LoadResult.Error).throwable)
  }

  @Test
  fun `отмена загрузки не превращается в ошибку Paging`() = runTest {
    val repo = Repository()
    val cancellation = CancellationException("Cancelled")
    repo.failure = cancellation
    try {
      CharacterPagingSource(repo).load(PagingSource.LoadParams.Refresh(null, 20, false))
      fail("Expected cancellation")
    } catch (actual: CancellationException) {
      assertSame(cancellation, actual)
    }
  }

  @Test
  fun `refresh восстанавливает страницу у позиции прокрутки`() {
    val source = CharacterPagingSource(Repository())
    val page = PagingSource.LoadResult.Page(listOf(character(21)), 1, null)
    val state = PagingState(listOf(page), 0, PagingConfig(20), 0)
    assertEquals(2, source.getRefreshKey(state))
  }

  @Test
  fun `Pager подгружает страницы и повторная подписка использует кэш модели`() = runTest {
    val repo = Repository()
    val model = MainModel(backgroundScope, repo)
    val first = model.characters.asSnapshot { appendScrollWhile { true } }
    assertEquals((1..40).toList(), first.map { it.id })
    assertEquals(listOf(1, 2), repo.calls)
    val second = model.characters.asSnapshot { appendScrollWhile { true } }
    assertEquals(first, second)
    assertEquals(listOf(1, 2), repo.calls)
  }

  @Test
  fun `обновление создаёт новый PagingSource и читает свежие данные`() = runTest {
    val repo = Repository()
    val model = MainModel(backgroundScope, repo)
    var requestsBeforeRefresh = 0
    val snapshot = model.characters.asSnapshot {
      requestsBeforeRefresh = repo.calls.size
      repo.name = "Updated"
      refresh()
      // cachedIn delivers the new generation through the model's background scope.
      testScheduler.runCurrent()
      appendScrollWhile { true }
    }
    assertTrue(repo.calls.size > requestsBeforeRefresh)
    assertTrue(snapshot.isNotEmpty())
    assertTrue(snapshot.all { it.name == "Updated" })
  }

  private class Repository : CharacterRepository {
    val calls = mutableListOf<Int>()
    var failure: Exception? = null
    var name = "Rick"
    override suspend fun readPage(page: Int): CharacterPage {
      calls += page
      failure?.let { throw it }
      return CharacterPage(
        ((page - 1) * 20 + 1..page * 20).map { character(it).copy(name = name) },
        page,
        40,
        2,
        if (page < 2) page + 1 else null,
        if (page > 1) page - 1 else null,
      )
    }
    override suspend fun readCharacter(id: Int): Character = error("Unused")
  }
}

private fun character(id: Int) = Character(
  id,
  "Rick",
  CharacterStatus.Alive,
  "Human",
  "",
  CharacterGender.Male,
  CharacterLocation("Earth", ""),
  CharacterLocation("Citadel", ""),
  "",
)
