package com.swahilib.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.HistoryDao_Impl
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.IdiomDao_Impl
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.ProverbDao_Impl
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SayingDao_Impl
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.SearchDao_Impl
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.daos.WordDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _historyDao: Lazy<HistoryDao> = lazy {
    HistoryDao_Impl(this)
  }

  private val _idiomDao: Lazy<IdiomDao> = lazy {
    IdiomDao_Impl(this)
  }

  private val _proverbDao: Lazy<ProverbDao> = lazy {
    ProverbDao_Impl(this)
  }

  private val _sayingDao: Lazy<SayingDao> = lazy {
    SayingDao_Impl(this)
  }

  private val _searchDao: Lazy<SearchDao> = lazy {
    SearchDao_Impl(this)
  }

  private val _wordDao: Lazy<WordDao> = lazy {
    WordDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "8f10f9da9b390a70f3c2a865b760aae2", "c9e163022d89a98d7c0a0dd5cf10cd58") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `item` INTEGER NOT NULL, `type` TEXT NOT NULL, `createdAt` TEXT NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_history_id` ON `history` (`id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `idioms` (`rid` INTEGER NOT NULL, `title` TEXT, `meaning` TEXT, `views` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `liked` INTEGER NOT NULL, `createdAt` TEXT, `updatedAt` TEXT, PRIMARY KEY(`rid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_idioms_rid` ON `idioms` (`rid`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `proverbs` (`rid` INTEGER NOT NULL, `title` TEXT, `synonyms` TEXT, `meaning` TEXT, `conjugation` TEXT, `views` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `liked` INTEGER NOT NULL, `createdAt` TEXT, `updatedAt` TEXT, PRIMARY KEY(`rid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_proverbs_rid` ON `proverbs` (`rid`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sayings` (`rid` INTEGER NOT NULL, `title` TEXT, `meaning` TEXT, `views` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `liked` INTEGER NOT NULL, `createdAt` TEXT, `updatedAt` TEXT, PRIMARY KEY(`rid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_sayings_rid` ON `sayings` (`rid`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `search` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `createdAt` TEXT NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_search_id` ON `search` (`id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `words` (`rid` INTEGER NOT NULL, `title` TEXT, `synonyms` TEXT, `meaning` TEXT, `conjugation` TEXT, `views` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `liked` INTEGER NOT NULL, `createdAt` TEXT, `updatedAt` TEXT, PRIMARY KEY(`rid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_words_rid` ON `words` (`rid`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8f10f9da9b390a70f3c2a865b760aae2')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `history`")
        connection.execSQL("DROP TABLE IF EXISTS `idioms`")
        connection.execSQL("DROP TABLE IF EXISTS `proverbs`")
        connection.execSQL("DROP TABLE IF EXISTS `sayings`")
        connection.execSQL("DROP TABLE IF EXISTS `search`")
        connection.execSQL("DROP TABLE IF EXISTS `words`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistory.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("item", TableInfo.Column("item", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesHistory.add(TableInfo.Index("index_history_id", true, listOf("id"), listOf("ASC")))
        val _infoHistory: TableInfo = TableInfo("history", _columnsHistory, _foreignKeysHistory, _indicesHistory)
        val _existingHistory: TableInfo = read(connection, "history")
        if (!_infoHistory.equals(_existingHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |history(com.swahilib.core.database.model.HistoryEntity).
              | Expected:
              |""".trimMargin() + _infoHistory + """
              |
              | Found:
              |""".trimMargin() + _existingHistory)
        }
        val _columnsIdioms: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsIdioms.put("rid", TableInfo.Column("rid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("title", TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("meaning", TableInfo.Column("meaning", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("views", TableInfo.Column("views", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("liked", TableInfo.Column("liked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("createdAt", TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIdioms.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysIdioms: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesIdioms: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesIdioms.add(TableInfo.Index("index_idioms_rid", true, listOf("rid"), listOf("ASC")))
        val _infoIdioms: TableInfo = TableInfo("idioms", _columnsIdioms, _foreignKeysIdioms, _indicesIdioms)
        val _existingIdioms: TableInfo = read(connection, "idioms")
        if (!_infoIdioms.equals(_existingIdioms)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |idioms(com.swahilib.core.database.model.IdiomEntity).
              | Expected:
              |""".trimMargin() + _infoIdioms + """
              |
              | Found:
              |""".trimMargin() + _existingIdioms)
        }
        val _columnsProverbs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProverbs.put("rid", TableInfo.Column("rid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("title", TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("synonyms", TableInfo.Column("synonyms", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("meaning", TableInfo.Column("meaning", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("conjugation", TableInfo.Column("conjugation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("views", TableInfo.Column("views", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("liked", TableInfo.Column("liked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("createdAt", TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProverbs.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProverbs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProverbs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProverbs.add(TableInfo.Index("index_proverbs_rid", true, listOf("rid"), listOf("ASC")))
        val _infoProverbs: TableInfo = TableInfo("proverbs", _columnsProverbs, _foreignKeysProverbs, _indicesProverbs)
        val _existingProverbs: TableInfo = read(connection, "proverbs")
        if (!_infoProverbs.equals(_existingProverbs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |proverbs(com.swahilib.core.database.model.ProverbEntity).
              | Expected:
              |""".trimMargin() + _infoProverbs + """
              |
              | Found:
              |""".trimMargin() + _existingProverbs)
        }
        val _columnsSayings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSayings.put("rid", TableInfo.Column("rid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("title", TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("meaning", TableInfo.Column("meaning", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("views", TableInfo.Column("views", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("liked", TableInfo.Column("liked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("createdAt", TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSayings.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSayings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSayings: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSayings.add(TableInfo.Index("index_sayings_rid", true, listOf("rid"), listOf("ASC")))
        val _infoSayings: TableInfo = TableInfo("sayings", _columnsSayings, _foreignKeysSayings, _indicesSayings)
        val _existingSayings: TableInfo = read(connection, "sayings")
        if (!_infoSayings.equals(_existingSayings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sayings(com.swahilib.core.database.model.SayingEntity).
              | Expected:
              |""".trimMargin() + _infoSayings + """
              |
              | Found:
              |""".trimMargin() + _existingSayings)
        }
        val _columnsSearch: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSearch.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSearch.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSearch.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSearch: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSearch: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSearch.add(TableInfo.Index("index_search_id", true, listOf("id"), listOf("ASC")))
        val _infoSearch: TableInfo = TableInfo("search", _columnsSearch, _foreignKeysSearch, _indicesSearch)
        val _existingSearch: TableInfo = read(connection, "search")
        if (!_infoSearch.equals(_existingSearch)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |search(com.swahilib.core.database.model.SearchEntity).
              | Expected:
              |""".trimMargin() + _infoSearch + """
              |
              | Found:
              |""".trimMargin() + _existingSearch)
        }
        val _columnsWords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWords.put("rid", TableInfo.Column("rid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("title", TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("synonyms", TableInfo.Column("synonyms", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("meaning", TableInfo.Column("meaning", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("conjugation", TableInfo.Column("conjugation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("views", TableInfo.Column("views", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("liked", TableInfo.Column("liked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("createdAt", TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWords.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWords.add(TableInfo.Index("index_words_rid", true, listOf("rid"), listOf("ASC")))
        val _infoWords: TableInfo = TableInfo("words", _columnsWords, _foreignKeysWords, _indicesWords)
        val _existingWords: TableInfo = read(connection, "words")
        if (!_infoWords.equals(_existingWords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |words(com.swahilib.core.database.model.WordEntity).
              | Expected:
              |""".trimMargin() + _infoWords + """
              |
              | Found:
              |""".trimMargin() + _existingWords)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "history", "idioms", "proverbs", "sayings", "search", "words")
  }

  public override fun clearAllTables() {
    super.performClear(false, "history", "idioms", "proverbs", "sayings", "search", "words")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(HistoryDao::class, HistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(IdiomDao::class, IdiomDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProverbDao::class, ProverbDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SayingDao::class, SayingDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SearchDao::class, SearchDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WordDao::class, WordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun historiesDao(): HistoryDao = _historyDao.value

  public override fun idiomsDao(): IdiomDao = _idiomDao.value

  public override fun proverbsDao(): ProverbDao = _proverbDao.value

  public override fun sayingsDao(): SayingDao = _sayingDao.value

  public override fun searchesDao(): SearchDao = _searchDao.value

  public override fun wordsDao(): WordDao = _wordDao.value
}
