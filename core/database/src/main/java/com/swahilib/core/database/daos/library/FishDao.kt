package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.FishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FishEntity>)

    @Query("DELETE FROM fish")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<FishEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM fish ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<FishEntity>>

    @Query("SELECT COUNT(*) FROM fish")
    suspend fun count(): Int
}
