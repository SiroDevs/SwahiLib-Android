package com.swahilib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.swahilib.core.database.model.SayingEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SayingDao_Impl(
  __db: RoomDatabase,
) : SayingDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSayingEntity: EntityInsertAdapter<SayingEntity>

  private val __updateAdapterOfSayingEntity: EntityDeleteOrUpdateAdapter<SayingEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSayingEntity = object : EntityInsertAdapter<SayingEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `sayings` (`rid`,`title`,`meaning`,`views`,`likes`,`liked`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SayingEntity) {
        statement.bindLong(1, entity.rid.toLong())
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpMeaning: String? = entity.meaning
        if (_tmpMeaning == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpMeaning)
        }
        statement.bindLong(4, entity.views.toLong())
        statement.bindLong(5, entity.likes.toLong())
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpCreatedAt: String? = entity.createdAt
        if (_tmpCreatedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCreatedAt)
        }
        val _tmpUpdatedAt: String? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpUpdatedAt)
        }
      }
    }
    this.__updateAdapterOfSayingEntity = object : EntityDeleteOrUpdateAdapter<SayingEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `sayings` SET `rid` = ?,`title` = ?,`meaning` = ?,`views` = ?,`likes` = ?,`liked` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `rid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SayingEntity) {
        statement.bindLong(1, entity.rid.toLong())
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpMeaning: String? = entity.meaning
        if (_tmpMeaning == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpMeaning)
        }
        statement.bindLong(4, entity.views.toLong())
        statement.bindLong(5, entity.likes.toLong())
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpCreatedAt: String? = entity.createdAt
        if (_tmpCreatedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCreatedAt)
        }
        val _tmpUpdatedAt: String? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpUpdatedAt)
        }
        statement.bindLong(9, entity.rid.toLong())
      }
    }
  }

  public override suspend fun insert(saying: SayingEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSayingEntity.insert(_connection, saying)
  }

  public override suspend fun insertAll(sayings: List<SayingEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSayingEntity.insert(_connection, sayings)
  }

  public override fun update(saying: SayingEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfSayingEntity.handle(_connection, saying)
  }

  public override fun getById(rid: String): Flow<SayingEntity> {
    val _sql: String = "SELECT * FROM sayings WHERE rid = ?"
    return createFlow(__db, false, arrayOf("sayings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, rid)
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SayingEntity
        if (_stmt.step()) {
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpCreatedAt: String?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmpCreatedAt = null
          } else {
            _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          _result = SayingEntity(_tmpRid,_tmpTitle,_tmpMeaning,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.swahilib.core.database.model.SayingEntity'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchSayingByTitle(title: String?): Flow<List<SayingEntity>> {
    val _sql: String = "SELECT * FROM sayings WHERE title LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("sayings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (title == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, title)
        }
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SayingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SayingEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpCreatedAt: String?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmpCreatedAt = null
          } else {
            _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          _item = SayingEntity(_tmpRid,_tmpTitle,_tmpMeaning,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSayingsByTitles(titles: List<String>): Flow<List<SayingEntity>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM sayings WHERE title IN (")
    val _inputSize: Int = titles.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return createFlow(__db, false, arrayOf("sayings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in titles) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SayingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: SayingEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpCreatedAt: String?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmpCreatedAt = null
          } else {
            _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          _item_1 = SayingEntity(_tmpRid,_tmpTitle,_tmpMeaning,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAll(): Flow<List<SayingEntity>> {
    val _sql: String = "SELECT * FROM sayings"
    return createFlow(__db, false, arrayOf("sayings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SayingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SayingEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpCreatedAt: String?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmpCreatedAt = null
          } else {
            _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          _item = SayingEntity(_tmpRid,_tmpTitle,_tmpMeaning,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun delete() {
    val _sql: String = "DELETE FROM sayings"
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
