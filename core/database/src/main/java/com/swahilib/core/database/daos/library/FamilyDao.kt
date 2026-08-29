package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.FamilyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FamilyEntity>)

    @Query("DELETE FROM family")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<FamilyEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM family ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<FamilyEntity>>

    @Query("SELECT COUNT(*) FROM family")
    suspend fun count(): Int
}
