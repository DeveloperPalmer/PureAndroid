package ru.sla.pure.android.feature.main.data

import android.app.Application
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.database.CharacterDatabase
import ru.sla.pure.android.feature.main.domain.CharacterRepository
import ru.sla.pure.android.openapi.schema.api.CharacterApi
import ru.sla.pure.android.openapi.schema.model.CharacterLocationNM
import ru.sla.pure.android.openapi.schema.model.CharacterNM
import ru.sla.pure.android.openapi.schema.model.CharacterPageNM
import ru.sla.pure.android.openapi.schema.model.PageInfoNM

@RunWith(AndroidJUnit4::class)
class CharacterCacheIntegrationTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun pageSurvivesDatabaseReopenAndCanBeReadOffline() = runTest {
        val name = "character-repository-test.db"
        context.deleteDatabase(name)
        val api = object : CharacterApi {
            override suspend fun readCharacter(id: Int) = CharacterNM(
                id, "Rick", "Alive", "Human", "", "Male",
                CharacterLocationNM("Earth", "origin"), CharacterLocationNM("Citadel", "location"), "image",
            )
            override suspend fun readPage(page: Int) = CharacterPageNM(
                PageInfoNM(40, 2, "next", null), listOf(readCharacter(2), readCharacter(1)),
            )
        }
        val offline = object : CharacterApi {
            override suspend fun readCharacter(id: Int): CharacterNM = throw ServerException.Unreachable(null)
            override suspend fun readPage(page: Int): CharacterPageNM = throw ServerException.Unreachable(null)
        }
        var database = Room.databaseBuilder(context, CharacterDatabase::class.java, name).build()
        try {
            val expected = CharacterRepositoryImpl(api, database.characterDao()).readPage(1)
            database.close()
            database = Room.databaseBuilder(context, CharacterDatabase::class.java, name).build()
            val repository = CharacterRepositoryImpl(offline, database.characterDao())
            assertEquals(expected, repository.readPage(1))
            assertEquals(expected.characters.first(), repository.readCharacter(2))
        } finally {
            database.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun metroCreatesRepositoryAndSharesDatabaseInAppScope() = runTest {
        val application = context.applicationContext as Application
        val graph = createGraphFactory<CacheTestGraph.Factory>().create(application)
        try {
            assertSame(graph.repository, graph.repository)
            assertSame(graph.database, graph.database)
            assertTrue(graph.repository is CharacterRepositoryImpl)
            try {
                graph.repository.readPage(0)
                fail("Expected invalid page")
            } catch (_: IllegalArgumentException) {
                // Resolving the repository also resolves the production API and DAO bindings.
            }
        } finally {
            graph.database.close()
        }
    }
}

@DependencyGraph(AppScope::class)
internal interface CacheTestGraph {
    val repository: CharacterRepository
    val database: CharacterDatabase

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides application: Application): CacheTestGraph
    }
}
