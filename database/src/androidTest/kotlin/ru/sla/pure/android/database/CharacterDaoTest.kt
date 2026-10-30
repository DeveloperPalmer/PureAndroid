package ru.sla.pure.android.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.sla.pure.android.database.entity.CharacterEntity
import ru.sla.pure.android.database.entity.CharacterPageEntity

@RunWith(AndroidJUnit4::class)
class CharacterDaoTest {
    private lateinit var database: CharacterDatabase
    private val dao get() = database.characterDao()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            CharacterDatabase::class.java,
        ).build()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun missingPageDiffersFromCachedEmptyPage() = runTest {
        assertNull(dao.readPage(1))
        val info = page(1)
        dao.savePage(info, emptyList())
        assertEquals(info, dao.readPage(1)?.info)
        assertEquals(emptyList<CharacterEntity>(), dao.readPage(1)?.characters)
    }

    @Test
    fun refreshReplacesPageMembershipAndPreservesOtherPages() = runTest {
        dao.savePage(page(1), listOf(character(3), character(1)))
        dao.savePage(page(2), listOf(character(2)))
        assertEquals(listOf(3, 1), dao.readPage(1)?.characters?.map { it.id })

        dao.savePage(page(1), listOf(character(4)))

        assertEquals(listOf(4), dao.readPage(1)?.characters?.map { it.id })
        assertEquals(listOf(2), dao.readPage(2)?.characters?.map { it.id })
        assertNotNull(dao.readCharacter(3))
    }

    @Test
    fun updatingCharacterPreservesMembershipAndUpdatesEveryPage() = runTest {
        dao.savePage(page(1), listOf(character(1)))
        dao.savePage(page(2), listOf(character(1)))
        val updated = character(1).copy(name = "Updated")
        dao.saveCharacter(updated)
        assertEquals(updated, dao.readCharacter(1))
        assertEquals(updated, dao.readPage(1)?.characters?.single())
        assertEquals(updated, dao.readPage(2)?.characters?.single())
    }

    @Test
    fun failedRefreshRollsBackCharactersMetadataAndMembership() = runTest {
        val original = character(1)
        dao.savePage(page(1), listOf(original))
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_entry BEFORE INSERT ON character_page_entries
            WHEN NEW.characterId = 2 BEGIN SELECT RAISE(ABORT, 'test failure'); END
        """)
        try {
            dao.savePage(page(1).copy(totalCount = 100), listOf(character(2)))
            fail("Expected a failed transaction")
        } catch (_: android.database.sqlite.SQLiteException) {
            // Failure occurs after character and page metadata have been written.
        }
        assertEquals(page(1), dao.readPage(1)?.info)
        assertEquals(listOf(original), dao.readPage(1)?.characters)
        assertNull(dao.readCharacter(2))
    }

    private fun page(number: Int) = CharacterPageEntity(number, 40, 2, null, null)

    private fun character(id: Int) = CharacterEntity(
        id, "Character $id", "Alive", "Human", "", "Unknown",
        "Earth", "", "Citadel", "", "https://example.com/$id.png",
    )
}
