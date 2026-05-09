package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.SayingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SayingDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(saying: SayingEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(sayings: List<SayingEntity>)

    @Update
    fun update(saying: SayingEntity)

    @Query("SELECT * FROM sayings WHERE rid = :rid")
    fun getById(rid: String): Flow<SayingEntity>

    @Query("DELETE FROM sayings")
    fun delete()

    @Query("SELECT * FROM sayings WHERE title LIKE '%' || :title || '%'")
    fun searchSayingByTitle(title: String?): Flow<List<SayingEntity>>

    @Query("SELECT * FROM sayings WHERE title IN (:titles)")
    fun getSayingsByTitles(titles: List<String>): Flow<List<SayingEntity>>

    @Query("SELECT * FROM sayings")
    fun getAll(): Flow<List<SayingEntity>>
}