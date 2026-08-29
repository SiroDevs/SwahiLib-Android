package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.KidGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KidGamesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<KidGameEntity>)

    @Query("DELETE FROM kid_games")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<KidGameEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM kid_games ORDER BY orderIndex ASC")
    fun getAll(): Flow<List<KidGameEntity>>

    @Query("SELECT COUNT(*) FROM kid_games")
    suspend fun count(): Int
}
