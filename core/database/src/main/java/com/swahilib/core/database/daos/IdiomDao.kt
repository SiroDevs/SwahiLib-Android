package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.IdiomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdiomDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(idiomEntity: IdiomEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(idioms: List<IdiomEntity>)

    @Update
    fun update(idiomEntity: IdiomEntity)

    @Query("SELECT * FROM idioms WHERE rid = :rid")
    fun getById(rid: String): Flow<IdiomEntity>

    @Query("SELECT * FROM idioms WHERE rid = :rid LIMIT 1")
    suspend fun getByRid(rid: Int): IdiomEntity?

    @Query("DELETE FROM idioms")
    fun delete()

    @Query("UPDATE idioms SET liked = 0 WHERE liked = 1")
    suspend fun clearAllLiked()

    @Query("SELECT * FROM idioms WHERE title LIKE '%' || :title || '%'")
    fun searchIdiomByTitle(title: String?): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms WHERE title IN (:titles)")
    fun getIdiomsByTitles(titles: List<String>): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms")
    fun getAll(): Flow<List<IdiomEntity>>
}