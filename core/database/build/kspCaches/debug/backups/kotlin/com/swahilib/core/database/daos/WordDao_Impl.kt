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
import com.swahilib.core.database.model.WordEntity
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
public class WordDao_Impl(
  __db: RoomDatabase,
) : WordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWordEntity: EntityInsertAdapter<WordEntity>

  private val __updateAdapterOfWordEntity: EntityDeleteOrUpdateAdapter<WordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWordEntity = object : EntityInsertAdapter<WordEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `words` (`rid`,`title`,`synonyms`,`meaning`,`conjugation`,`views`,`likes`,`liked`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WordEntity) {
        statement.bindLong(1, entity.rid.toLong())
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpSynonyms: String? = entity.synonyms
        if (_tmpSynonyms == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSynonyms)
        }
        val _tmpMeaning: String? = entity.meaning
        if (_tmpMeaning == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMeaning)
        }
        val _tmpConjugation: String? = entity.conjugation
        if (_tmpConjugation == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpConjugation)
        }
        statement.bindLong(6, entity.views.toLong())
        statement.bindLong(7, entity.likes.toLong())
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpCreatedAt: String? = entity.createdAt
        if (_tmpCreatedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpCreatedAt)
        }
        val _tmpUpdatedAt: String? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpUpdatedAt)
        }
      }
    }
    this.__updateAdapterOfWordEntity = object : EntityDeleteOrUpdateAdapter<WordEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `words` SET `rid` = ?,`title` = ?,`synonyms` = ?,`meaning` = ?,`conjugation` = ?,`views` = ?,`likes` = ?,`liked` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `rid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WordEntity) {
        statement.bindLong(1, entity.rid.toLong())
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpSynonyms: String? = entity.synonyms
        if (_tmpSynonyms == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSynonyms)
        }
        val _tmpMeaning: String? = entity.meaning
        if (_tmpMeaning == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMeaning)
        }
        val _tmpConjugation: String? = entity.conjugation
        if (_tmpConjugation == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpConjugation)
        }
        statement.bindLong(6, entity.views.toLong())
        statement.bindLong(7, entity.likes.toLong())
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpCreatedAt: String? = entity.createdAt
        if (_tmpCreatedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpCreatedAt)
        }
        val _tmpUpdatedAt: String? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpUpdatedAt)
        }
        statement.bindLong(11, entity.rid.toLong())
      }
    }
  }

  public override suspend fun insert(word: WordEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWordEntity.insert(_connection, word)
  }

  public override suspend fun insertAll(words: List<WordEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWordEntity.insert(_connection, words)
  }

  public override fun update(word: WordEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfWordEntity.handle(_connection, word)
  }

  public override fun getById(rid: String): Flow<WordEntity> {
    val _sql: String = "SELECT * FROM words WHERE rid = ?"
    return createFlow(__db, false, arrayOf("words")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, rid)
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSynonyms: Int = getColumnIndexOrThrow(_stmt, "synonyms")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfConjugation: Int = getColumnIndexOrThrow(_stmt, "conjugation")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: WordEntity
        if (_stmt.step()) {
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpSynonyms: String?
          if (_stmt.isNull(_columnIndexOfSynonyms)) {
            _tmpSynonyms = null
          } else {
            _tmpSynonyms = _stmt.getText(_columnIndexOfSynonyms)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpConjugation: String?
          if (_stmt.isNull(_columnIndexOfConjugation)) {
            _tmpConjugation = null
          } else {
            _tmpConjugation = _stmt.getText(_columnIndexOfConjugation)
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
          _result = WordEntity(_tmpRid,_tmpTitle,_tmpSynonyms,_tmpMeaning,_tmpConjugation,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.swahilib.core.database.model.WordEntity'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchWordByTitle(title: String?): Flow<List<WordEntity>> {
    val _sql: String = "SELECT * FROM words WHERE title LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("words")) { _connection ->
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
        val _columnIndexOfSynonyms: Int = getColumnIndexOrThrow(_stmt, "synonyms")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfConjugation: Int = getColumnIndexOrThrow(_stmt, "conjugation")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<WordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WordEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpSynonyms: String?
          if (_stmt.isNull(_columnIndexOfSynonyms)) {
            _tmpSynonyms = null
          } else {
            _tmpSynonyms = _stmt.getText(_columnIndexOfSynonyms)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpConjugation: String?
          if (_stmt.isNull(_columnIndexOfConjugation)) {
            _tmpConjugation = null
          } else {
            _tmpConjugation = _stmt.getText(_columnIndexOfConjugation)
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
          _item = WordEntity(_tmpRid,_tmpTitle,_tmpSynonyms,_tmpMeaning,_tmpConjugation,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWordsByTitles(titles: List<String>): Flow<List<WordEntity>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM words WHERE title IN (")
    val _inputSize: Int = titles.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return createFlow(__db, false, arrayOf("words")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in titles) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSynonyms: Int = getColumnIndexOrThrow(_stmt, "synonyms")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfConjugation: Int = getColumnIndexOrThrow(_stmt, "conjugation")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<WordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: WordEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpSynonyms: String?
          if (_stmt.isNull(_columnIndexOfSynonyms)) {
            _tmpSynonyms = null
          } else {
            _tmpSynonyms = _stmt.getText(_columnIndexOfSynonyms)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpConjugation: String?
          if (_stmt.isNull(_columnIndexOfConjugation)) {
            _tmpConjugation = null
          } else {
            _tmpConjugation = _stmt.getText(_columnIndexOfConjugation)
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
          _item_1 = WordEntity(_tmpRid,_tmpTitle,_tmpSynonyms,_tmpMeaning,_tmpConjugation,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAll(): Flow<List<WordEntity>> {
    val _sql: String = "SELECT * FROM words"
    return createFlow(__db, false, arrayOf("words")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRid: Int = getColumnIndexOrThrow(_stmt, "rid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSynonyms: Int = getColumnIndexOrThrow(_stmt, "synonyms")
        val _columnIndexOfMeaning: Int = getColumnIndexOrThrow(_stmt, "meaning")
        val _columnIndexOfConjugation: Int = getColumnIndexOrThrow(_stmt, "conjugation")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<WordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WordEntity
          val _tmpRid: Int
          _tmpRid = _stmt.getLong(_columnIndexOfRid).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpSynonyms: String?
          if (_stmt.isNull(_columnIndexOfSynonyms)) {
            _tmpSynonyms = null
          } else {
            _tmpSynonyms = _stmt.getText(_columnIndexOfSynonyms)
          }
          val _tmpMeaning: String?
          if (_stmt.isNull(_columnIndexOfMeaning)) {
            _tmpMeaning = null
          } else {
            _tmpMeaning = _stmt.getText(_columnIndexOfMeaning)
          }
          val _tmpConjugation: String?
          if (_stmt.isNull(_columnIndexOfConjugation)) {
            _tmpConjugation = null
          } else {
            _tmpConjugation = _stmt.getText(_columnIndexOfConjugation)
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
          _item = WordEntity(_tmpRid,_tmpTitle,_tmpSynonyms,_tmpMeaning,_tmpConjugation,_tmpViews,_tmpLikes,_tmpLiked,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun delete() {
    val _sql: String = "DELETE FROM words"
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
