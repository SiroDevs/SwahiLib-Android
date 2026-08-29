package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.CapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CapEntity>)

    @Query("DELETE FROM caps")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<CapEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM caps ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<CapEntity>>

    @Query("SELECT COUNT(*) FROM caps")
    suspend fun count(): Int
}
