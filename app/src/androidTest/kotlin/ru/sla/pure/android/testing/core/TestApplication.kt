package ru.sla.pure.android.testing.core

import androidx.room.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import ru.sla.pure.android.MainActivity
import ru.sla.pure.android.PureApplication
import ru.sla.pure.android.app.data.di.ApiBaseUrl
import ru.sla.pure.android.app.data.di.DatabaseModule
import ru.sla.pure.android.app.data.di.NetworkConfigurationModule
import ru.sla.pure.android.database.CharacterDatabase
import ru.sla.pure.android.database.dao.CharacterDao
import ru.sla.pure.android.feature.app.di.AppFlowComponent
import ru.sla.pure.android.testing.AppUnderTest

class TestApplication : PureApplication(), AppUnderTest {
  private var testFlow: AppFlowComponent? = null
  private var testDatabase: CharacterDatabase? = null

  override val appFlowComponent: AppFlowComponent
    get() = checkNotNull(testFlow) { "Set up the test graph before launching MainActivity" }

  override fun install(baseUrl: String) {
    check(testFlow == null) { "The test graph is already installed" }
    val database = Room.inMemoryDatabaseBuilder(this, CharacterDatabase::class.java).build()
    testDatabase = database
    testFlow = createGraphFactory<TestAppGraph.Factory>()
      .create(
        apiBaseUrl = ApiBaseUrl(baseUrl),
        database = database,
      )
      .appFlowFactory()
      .create()
  }

  override fun reset() {
    testFlow = null
    testDatabase?.close()
    testDatabase = null
  }

  override fun mainActivityClass() = MainActivity::class.java
}

@DependencyGraph(
  AppScope::class,
  excludes = [NetworkConfigurationModule::class, DatabaseModule::class],
)
internal interface TestAppGraph {
  fun appFlowFactory(): AppFlowComponent.Factory

  @Provides
  @SingleIn(AppScope::class)
  fun provideCharacterDao(database: CharacterDatabase): CharacterDao = database.characterDao()

  @DependencyGraph.Factory
  interface Factory {
    fun create(
      @Provides apiBaseUrl: ApiBaseUrl,
      @Provides database: CharacterDatabase,
    ): TestAppGraph
  }
}
