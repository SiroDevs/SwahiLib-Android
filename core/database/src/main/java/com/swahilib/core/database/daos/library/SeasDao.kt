package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.SeasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SeasEntity>)

    @Query("DELETE FROM seas")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<SeasEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM seas ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<SeasEntity>>

    @Query("SELECT COUNT(*) FROM seas")
    suspend fun count(): Int
}
