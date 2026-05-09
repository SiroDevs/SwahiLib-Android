package com.swahilib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.swahilib.core.database.model.HistoryEntity
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
public class HistoryDao_Impl(
  __db: RoomDatabase,
) : HistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryEntity: EntityInsertAdapter<HistoryEntity>

  private val __updateAdapterOfHistoryEntity: EntityDeleteOrUpdateAdapter<HistoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryEntity = object : EntityInsertAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `history` (`id`,`item`,`type`,`createdAt`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.item.toLong())
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.createdAt)
      }
    }
    this.__updateAdapterOfHistoryEntity = object : EntityDeleteOrUpdateAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `history` SET `id` = ?,`item` = ?,`type` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.item.toLong())
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.createdAt)
        statement.bindLong(5, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(history: HistoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHistoryEntity.insert(_connection, history)
  }

  public override suspend fun insertAll(histories: List<HistoryEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHistoryEntity.insert(_connection, histories)
  }

  public override fun update(history: HistoryEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfHistoryEntity.handle(_connection, history)
  }

  public override fun getById(id: String): Flow<HistoryEntity> {
    val _sql: String = "SELECT * FROM history WHERE id = ?"
    return createFlow(__db, false, arrayOf("history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfItem: Int = getColumnIndexOrThrow(_stmt, "item")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: HistoryEntity
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpItem: Int
          _tmpItem = _stmt.getLong(_columnIndexOfItem).toInt()
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _result = HistoryEntity(_tmpId,_tmpItem,_tmpType,_tmpCreatedAt)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.swahilib.core.database.model.HistoryEntity'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAll(): Flow<List<HistoryEntity>> {
    val _sql: String = "SELECT * FROM history"
    return createFlow(__db, false, arrayOf("history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfItem: Int = getColumnIndexOrThrow(_stmt, "item")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<HistoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpItem: Int
          _tmpItem = _stmt.getLong(_columnIndexOfItem).toInt()
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item = HistoryEntity(_tmpId,_tmpItem,_tmpType,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun delete() {
    val _sql: String = "DELETE FROM history"
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
