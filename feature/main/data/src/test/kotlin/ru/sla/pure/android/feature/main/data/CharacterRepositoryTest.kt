package ru.sla.pure.android.feature.main.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.sla.pure.android.app.data.di.NetworkModule
import ru.sla.pure.android.core.domain.entity.ServerException
import ru.sla.pure.android.feature.main.domain.entity.CharacterGender
import ru.sla.pure.android.feature.main.domain.entity.CharacterStatus
import ru.sla.pure.android.openapi.schema.api.CharacterApi
import ru.sla.pure.android.openapi.schema.model.CharacterNM
import ru.sla.pure.android.openapi.schema.model.CharacterPageNM
import java.io.IOException
import java.util.concurrent.TimeUnit

class CharacterRepositoryTest {
  private val dao = InMemoryCharacterDao()
  private val server = MockWebServer()
  private val module = object : NetworkModule {}
  private val client = module.provideOkHttpClient()
  private lateinit var api: CharacterRepositoryImpl

  @Before
  fun setUp() {
    server.start()
    val retrofit = module.provideRetrofit(client).newBuilder()
      .baseUrl(server.url("/api/")).build()
    api = CharacterRepositoryImpl(retrofit.create(CharacterApi::class.java), dao)
  }

  @After
  fun tearDown() {
    server.close()
    client.connectionPool.evictAll()
    client.dispatcher.executorService.shutdown()
  }

  @Test
  fun `страница и персонажи доступны из кэша после потери сети`() = runTest {
    respond("""{"info":{"count":60,"pages":3,"next":"next","prev":"prev"},"results":[$characterJson]}""")
    val online = api.readPage(2)
    server.close()
    assertEquals(online, api.readPage(2))
    assertEquals(online.characters.single(), api.readCharacter(1))
    expectFailure<ServerException.Unreachable> { api.readPage(1) }
    expectFailure<ServerException.Unreachable> { api.readCharacter(2) }
  }

  @Test
  fun `повторный запрос обновляет кэш а другой репозиторий читает сохранённое`() = runTest {
    respond(characterJson)
    api.readCharacter(1)
    respond(characterJson.replace("Rick Sanchez", "Updated Rick"))
    val updated = api.readCharacter(1)
    assertEquals("Updated Rick", updated.name)
    val offline = CharacterRepositoryImpl(failingService(ServerException.Unreachable(null)), dao)
    assertEquals(updated, offline.readCharacter(1))
  }

  @Test
  fun `сохранённая пустая страница доступна офлайн`() = runTest {
    respond("""{"info":{"count":0,"pages":1,"next":null,"prev":null},"results":[]}""")
    val online = api.readPage(1)
    val offline = CharacterRepositoryImpl(failingService(ServerException.Unreachable(null)), dao)
    assertEquals(online, offline.readPage(1))
  }

  @Test
  fun `кэш не скрывает HTTP ошибки некорректный ответ и отмену`() = runTest {
    respond(characterJson)
    api.readCharacter(1)
    val errors = listOf(
      ServerException.ApiError(404, null, "Not found"),
      ServerException.ApiError(503, null, "Unavailable"),
      ServerException.Unauthorized(null),
      ServerException.Malformed(null),
      CancellationException("Cancelled"),
    )
    for (error in errors) {
      val failing = CharacterRepositoryImpl(failingService(error), dao)
      val actual = expectFailure<Exception> { failing.readCharacter(1) }
      assertSame(error, actual)
    }
  }

  @Test
  fun `ошибка записи кэша передаётся вызывающему коду`() = runTest {
    val failure = IllegalStateException("Disk failure")
    dao.writeFailure = failure
    respond(characterJson)
    assertSame(failure, expectFailure<IllegalStateException> { api.readCharacter(1) })
  }

  @Test
  fun `страница персонажей преобразуется в доменные модели`() = runTest {
    respond(
      """{"info":{"count":60,"pages":3,"next":"https://rickandmortyapi.com/api/character?page=3","prev":"https://rickandmortyapi.com/api/character?page=1"},"results":[$characterJson]}"""
    )

    val page = api.readPage(2)

    assertEquals("/api/character?page=2", requestPath())
    assertEquals(60, page.totalCount)
    assertEquals(3, page.totalPages)
    assertEquals(2, page.page)
    assertEquals(3, page.nextPage)
    assertEquals(1, page.previousPage)
    val character = page.characters.single()
    assertEquals(1, character.id)
    assertEquals("Rick Sanchez", character.name)
    assertEquals(CharacterStatus.Alive, character.status)
    assertEquals(CharacterGender.Male, character.gender)
    assertEquals("Earth", character.origin.name)
    assertEquals("Citadel", character.location.name)
    assertEquals("https://example.com/rick.png", character.imageUrl)
  }

  @Test
  fun `единственная страница не содержит соседних страниц`() = runTest {
    respond("""{"info":{"count":0,"pages":1,"next":null,"prev":null},"results":[]}""")
    val page = api.readPage()
    assertNull(page.nextPage)
    assertNull(page.previousPage)
    assertTrue(page.characters.isEmpty())
  }

  @Test
  fun `персонаж загружается по ID а новые значения перечислений становятся неизвестными`() = runTest {
    respond(characterJson.replace("Alive", "NewStatus").replace("Male", "NewGender"))
    val character = api.readCharacter(1)
    assertEquals("/api/character/1", requestPath())
    assertEquals(CharacterStatus.Unknown, character.status)
    assertEquals(CharacterGender.Unknown, character.gender)
  }

  @Test
  fun `ответ 404 преобразуется в ошибку сервера`() = runTest {
    server.enqueue(MockResponse.Builder().code(404).body("""{"error":"Character not found"}""").build())
    val error = expectFailure<ServerException.ApiError> { api.readCharacter(9999) }
    assertEquals(404, error.status)
    assertNull(error.code)
    assertEquals("Character not found", error.serverMessage)
  }

  @Test
  fun `неразбираемое тело ошибки не скрывает HTTP статус`() = runTest {
    server.enqueue(MockResponse.Builder().code(502).body("<html>Bad Gateway</html>").build())
    val error = expectFailure<ServerException.ApiError> { api.readPage() }
    assertEquals(502, error.status)
    assertNull(error.code)
    assertNull(error.serverMessage)
  }

  @Test
  fun `ответ 401 преобразуется в отказ сессии`() = runTest {
    server.enqueue(MockResponse.Builder().code(401).build())
    expectFailure<ServerException.Unauthorized> { api.readPage() }
  }

  @Test
  fun `некорректный JSON преобразуется в ошибку ответа`() = runTest {
    respond("""{"id":"wrong"}""")
    expectFailure<ServerException.Malformed> { api.readCharacter(1) }
  }

  @Test
  fun `ошибка соединения преобразуется в недоступность сервера`() = runTest {
    server.close()
    val error = expectFailure<ServerException.Unreachable> { api.readCharacter(1) }
    assertTrue(generateSequence(error as Throwable) { it.cause }.any { it is IOException })
  }

  @Test
  fun `отмена корутины не преобразуется в ошибку сервера`() = runTest {
    val cancellation = CancellationException("Отмена")
    val failing = CharacterRepositoryImpl(failingService(cancellation), dao)
    val error = expectFailure<CancellationException> { failing.readPage() }
    assertSame(cancellation, error)
  }

  @Test
  fun `неположительные страница и ID отклоняются до запроса`() = runTest {
    expectFailure<IllegalArgumentException> { api.readPage(0) }
    expectFailure<IllegalArgumentException> { api.readCharacter(-1) }
    assertEquals(0, server.requestCount)
  }

  private fun respond(body: String) {
    server.enqueue(MockResponse.Builder().body(body).addHeader("Content-Type", "application/json").build())
  }

  private fun requestPath() = requireNotNull(server.takeRequest(5, TimeUnit.SECONDS)).target

  private fun failingService(error: Exception) = object : CharacterApi {
    override suspend fun readPage(page: Int): CharacterPageNM = throw error
    override suspend fun readCharacter(id: Int): CharacterNM = throw error
  }

  private suspend inline fun <reified T : Throwable> expectFailure(block: suspend () -> Any): T {
    try {
      block()
    } catch (error: Throwable) {
      if (error is T) return error
      throw error
    }
    throw AssertionError("Ожидалось исключение ${T::class.simpleName}")
  }
}

private val characterJson = """
    {"id":1,"name":"Rick Sanchez","status":"Alive","species":"Human","type":"",
    "gender":"Male","origin":{"name":"Earth","url":""},
    "location":{"name":"Citadel","url":"https://rickandmortyapi.com/api/location/3"},
    "image":"https://example.com/rick.png","episode":[],"url":"","created":""}
""".trimIndent()
