package ru.sla.pure.android.app.data

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.http.GET
import ru.sla.pure.android.app.data.di.NetworkModule
import ru.sla.pure.android.core.domain.entity.ServerException
import java.util.concurrent.TimeUnit

class ServerTransportTest {
  private val server = MockWebServer()
  private val networkModule = object : NetworkModule {}
  private val client = networkModule.provideOkHttpClient()
  private lateinit var api: TestApi

  @Before
  fun setUp() {
    server.start()
    val retrofit = networkModule.provideRetrofit(client)
    assertEquals("https://rickandmortyapi.com/api/", retrofit.baseUrl().toString())
    api = retrofit.newBuilder()
      .baseUrl(server.url("/api/"))
      .build()
      .create(TestApi::class.java)
  }

  @After
  fun tearDown() {
    server.close()
    client.connectionPool.evictAll()
    client.dispatcher.executorService.shutdown()
  }

  @Test
  fun `относительный путь сохраняет префикс api а неизвестные поля игнорируются`() = runTest {
    server.enqueue(
      MockResponse.Builder()
        .body("""{"id":1,"name":"Rick Sanchez","newField":true}""")
        .addHeader("Content-Type", "application/json")
        .build()
    )

    val result = api.character()

    assertEquals(1, result.id)
    assertEquals("Rick Sanchez", result.name)
    assertNull(result.description)
    val request = requireNotNull(server.takeRequest(5, TimeUnit.SECONDS))
    assertEquals("GET", request.method)
    assertEquals("/api/character/1", request.target)
  }

  @Test
  fun `неуспешный ответ возвращает HTTP ошибку`() = runTest {
    server.enqueue(
      MockResponse.Builder().code(404)
        .body("""{"error":"Character not found"}""").build()
    )

    val error = try {
      api.character()
      throw AssertionError("Expected HTTP error")
    } catch (error: ServerException.ApiError) {
      error
    }

    assertEquals(404, error.status)
  }

  interface TestApi {
    @GET("character/1")
    suspend fun character(): TestCharacter
  }

  @Serializable
  data class TestCharacter(val id: Int, val name: String, val description: String?)
}
