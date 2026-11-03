package ru.sla.pure.android.feature.main.domain

import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.sla.pure.android.core.domain.createCoroutineScope
import ru.sla.pure.android.feature.main.domain.entity.Character
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterLocation
import ru.sla.pure.android.feature.main.domain.entity.CharacterPage
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus

// Зеркалят PagingConfig в MainModel (pageSize = 20): меняется конфиг — меняем эти константы
private const val PAGE_COUNT = 2
private const val TOTAL_ITEMS = PAGE_SIZE * PAGE_COUNT

class MainModelTest {
  private val coroutineScope = createCoroutineScope()
  private lateinit var model: MainModel

  @BeforeEach
  fun setUp() {
    model = MainModel(
      coroutineScope = coroutineScope,
      repository = FakeCharacterRepository()
    )
  }

  @AfterEach
  fun tearDown() {
    coroutineScope.cancel()
  }

  @Test
  fun `первая страница отображается начиная с первого персонажа`() = runTest {
    val items = model.characters.asSnapshot()

    // prefetchDistance по умолчанию равен pageSize, поэтому снапшот может захватить и вторую
    // страницу — сравниваем только первые PAGE_SIZE элементов
    assertEquals(firstPageIds(), items.take(PAGE_SIZE).map { it.id })
  }

  @Test
  fun `вторая страница добавляется после первой`() = runTest {
    val items = model.characters.asSnapshot {
      // Скролл к последнему элементу первой страницы запускает догрузку следующей
      scrollTo(index = PAGE_SIZE - 1)
    }

    assertEquals((1..TOTAL_ITEMS).toList(), items.map { it.id })
  }

  private fun firstPageIds(): List<Int> = (1..PAGE_SIZE).toList()
}

private class FakeCharacterRepository : CharacterRepository {
  override suspend fun readPage(page: Int): CharacterPage = CharacterPage(
    characters = idsOfPage(page).map(::character),
    page = page,
    totalCount = TOTAL_ITEMS,
    totalPages = PAGE_COUNT,
    nextPage = if (page < PAGE_COUNT) page + 1 else null,
    previousPage = if (page > 1) page - 1 else null,
  )

  override suspend fun readCharacter(id: Int): Character = character(id)

  private fun idsOfPage(page: Int): IntRange = (page - 1) * PAGE_SIZE + 1..page * PAGE_SIZE

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
