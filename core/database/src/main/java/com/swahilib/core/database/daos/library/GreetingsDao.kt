package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.GreetingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GreetingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GreetingEntity>)

    @Query("DELETE FROM greetings")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<GreetingEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM greetings ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<GreetingEntity>>

    @Query("SELECT COUNT(*) FROM greetings")
    suspend fun count(): Int
}
