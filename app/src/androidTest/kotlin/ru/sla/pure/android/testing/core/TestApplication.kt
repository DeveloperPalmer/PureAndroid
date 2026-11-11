package ru.sla.pure.android.testing.core

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.runBlocking
import ru.sla.pure.android.MainActivity
import ru.sla.pure.android.PureApplication
import ru.sla.pure.android.app.data.di.ApiBaseUrl
import ru.sla.pure.android.app.data.di.DatabaseModule
import ru.sla.pure.android.app.data.di.NetworkConfigurationModule
import ru.sla.pure.android.auth.session.domain.AuthSessionRepository
import ru.sla.pure.android.auth.session.domain.entity.AccessToken
import ru.sla.pure.android.auth.session.domain.entity.AuthTokens
import ru.sla.pure.android.auth.session.domain.entity.RefreshToken
import ru.sla.pure.android.core.domain.di.ApplicationContext
import ru.sla.pure.android.core.domain.entity.UserId
import ru.sla.pure.android.database.CharacterDatabase
import ru.sla.pure.android.database.SettingsDatabase
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.database.dao.SettingsDao
import ru.sla.pure.android.di.ForegroundComponent
import ru.sla.pure.android.testing.AppUnderTest

class TestApplication : PureApplication(), AppUnderTest {
  private var testGraph: TestAppGraph? = null
  private var testDatabase: CharacterDatabase? = null
  private var testSettingsDatabase: SettingsDatabase? = null

  override val foregroundFactory: ForegroundComponent.Factory
    get() = checkNotNull(testGraph) { "Настройте тестовый граф до запуска MainActivity" }.foregroundFactory()

  override fun install(baseUrl: String) {
    check(testGraph == null) { "The test graph is already installed" }
    val database = Room.inMemoryDatabaseBuilder(this, CharacterDatabase::class.java)
      .setDriver(AndroidSQLiteDriver())
      .build()
    val settingsDatabase = Room.inMemoryDatabaseBuilder(this, SettingsDatabase::class.java)
      .setDriver(AndroidSQLiteDriver())
      .build()
    testDatabase = database
    testSettingsDatabase = settingsDatabase
    val graph = createGraphFactory<TestAppGraph.Factory>()
      .create(
        applicationContext = applicationContext,
        apiBaseUrl = ApiBaseUrl(baseUrl),
        database = database,
        settingsDatabase = settingsDatabase,
      )
    runBlocking {
      graph.authSessionRepository().startNew(
        tokens = AuthTokens(AccessToken("test-access"), RefreshToken("test-refresh"), System.currentTimeMillis()),
        userId = UserId("test-user"),
      )
    }
    testGraph = graph
  }

  override fun reset() {
    testGraph = null
    testDatabase?.close()
    testDatabase = null
    testSettingsDatabase?.close()
    testSettingsDatabase = null
  }

  override fun mainActivityClass() = MainActivity::class.java
}

@DependencyGraph(
  AppScope::class,
  excludes = [NetworkConfigurationModule::class, DatabaseModule::class],
  bindingContainers = [TestAppModule::class],
)
internal interface TestAppGraph {
  fun authSessionRepository(): AuthSessionRepository

  fun foregroundFactory(): ForegroundComponent.Factory

  @DependencyGraph.Factory
  interface Factory {
    fun create(
      @Provides @ApplicationContext applicationContext: Context,
      @Provides apiBaseUrl: ApiBaseUrl,
      @Provides database: CharacterDatabase,
      @Provides settingsDatabase: SettingsDatabase,
    ): TestAppGraph
  }
}

@BindingContainer
internal object TestAppModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideCharacterDao(database: CharacterDatabase): CharacterDao = database.characterDao()

  @Provides
  @SingleIn(AppScope::class)
  fun provideSettingsDao(database: SettingsDatabase): SettingsDao = database.settingsDao()
}
