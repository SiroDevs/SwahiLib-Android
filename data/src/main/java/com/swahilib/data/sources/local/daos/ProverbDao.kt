package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.data.models.Proverb
import kotlinx.coroutines.flow.Flow

@Dao
interface ProverbDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(proverb: Proverb)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(proverbs: List<Proverb>)

    @Update
    fun update(proverb: Proverb)

    @Query("SELECT * FROM proverbs WHERE rid = :rid")
    fun getById(rid: String): Flow<Proverb>

    @Query("DELETE FROM proverbs")
    fun delete()

    @Query("SELECT * FROM proverbs WHERE title LIKE '%' || :title || '%'")
    fun searchProverbByTitle(title: String?): Flow<List<Proverb>>

    @Query("SELECT * FROM proverbs WHERE title IN (:titles)")
    fun getProverbsByTitles(titles: List<String>): Flow<List<Proverb>>

    @Query("SELECT * FROM proverbs")
    fun getAll(): Flow<List<Proverb>>
}