package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.core.utils.Collections
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

    @Query("SELECT * FROM ${Collections.WORDS} WHERE rid = :id")
    fun getById(id: String): Flow<Proverb>

    @Query("DELETE FROM ${Collections.WORDS}")
    fun delete()

    @Query("SELECT * FROM ${Collections.WORDS} WHERE title LIKE '%' || :title || '%'")
    fun searchProverbByTitle(title: String?): Flow<List<Proverb>>

    @Query("SELECT * FROM ${Collections.WORDS}")
    fun getAll(): Flow<List<Proverb>>
}