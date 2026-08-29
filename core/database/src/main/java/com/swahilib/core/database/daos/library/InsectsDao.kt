package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.InsectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsectsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InsectEntity>)

    @Query("DELETE FROM insects")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<InsectEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM insects ORDER BY category ASC, orderIndex ASC")
    fun getAll(): Flow<List<InsectEntity>>

    @Query("SELECT COUNT(*) FROM insects")
    suspend fun count(): Int
}
