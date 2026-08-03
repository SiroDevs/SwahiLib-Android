package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 0 LIMIT 1")
    suspend fun get(): UserProgressEntity?

    @Query("SELECT * FROM user_progress WHERE id = 0 LIMIT 1")
    fun observe(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserProgressEntity)

    @Update
    suspend fun update(entity: UserProgressEntity)
}
