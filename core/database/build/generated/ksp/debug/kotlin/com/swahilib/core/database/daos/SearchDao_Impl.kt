package com.swahilib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.swahilib.core.database.model.SearchEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SearchDao_Impl(
  __db: RoomDatabase,
) : SearchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSearchEntity: EntityInsertAdapter<SearchEntity>

  private val __updateAdapterOfSearchEntity: EntityDeleteOrUpdateAdapter<SearchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSearchEntity = object : EntityInsertAdapter<SearchEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `search` (`id`,`title`,`createdAt`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SearchEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.createdAt)
      }
    }
    this.__updateAdapterOfSearchEntity = object : EntityDeleteOrUpdateAdapter<SearchEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `search` SET `id` = ?,`title` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SearchEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.createdAt)
        statement.bindLong(4, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(search: SearchEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSearchEntity.insert(_connection, search)
  }

  public override suspend fun insertAll(searches: List<SearchEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSearchEntity.insert(_connection, searches)
  }

  public override fun update(search: SearchEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfSearchEntity.handle(_connection, search)
  }

  public override fun getById(id: String): Flow<SearchEntity> {
    val _sql: String = "SELECT * FROM search WHERE id = ?"
    return createFlow(__db, false, arrayOf("search")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: SearchEntity
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _result = SearchEntity(_tmpId,_tmpTitle,_tmpCreatedAt)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.swahilib.core.database.model.SearchEntity'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchSearchByTitle(title: String?): Flow<List<SearchEntity>> {
    val _sql: String = "SELECT * FROM search WHERE title LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("search")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (title == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, title)
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<SearchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SearchEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item = SearchEntity(_tmpId,_tmpTitle,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAll(): Flow<List<SearchEntity>> {
    val _sql: String = "SELECT * FROM search"
    return createFlow(__db, false, arrayOf("search")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<SearchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SearchEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item = SearchEntity(_tmpId,_tmpTitle,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun delete() {
    val _sql: String = "DELETE FROM search"
    return performBlocking(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
