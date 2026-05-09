package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.ProverbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProverbDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(proverb: ProverbEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(proverbs: List<ProverbEntity>)

    @Update
    fun update(proverb: ProverbEntity)

    @Query("SELECT * FROM proverbs WHERE rid = :rid")
    fun getById(rid: String): Flow<ProverbEntity>

    @Query("DELETE FROM proverbs")
    fun delete()

    @Query("SELECT * FROM proverbs WHERE title LIKE '%' || :title || '%'")
    fun searchProverbByTitle(title: String?): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE title IN (:titles)")
    fun getProverbsByTitles(titles: List<String>): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs")
    fun getAll(): Flow<List<ProverbEntity>>
}