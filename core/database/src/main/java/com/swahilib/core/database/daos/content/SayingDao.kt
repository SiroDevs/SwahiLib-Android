/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.database.daos.content

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.entities.content.SayingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SayingDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(saying: SayingEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(sayings: List<SayingEntity>)

    @Update
    fun update(saying: SayingEntity)

    @Query("SELECT * FROM sayings WHERE rid = :rid")
    fun getById(rid: String): Flow<SayingEntity>

    @Query("SELECT * FROM sayings WHERE rid = :rid LIMIT 1")
    suspend fun getByRid(rid: Int): SayingEntity?

    @Query("DELETE FROM sayings")
    fun delete()

    @Query("UPDATE sayings SET liked = 0 WHERE liked = 1")
    suspend fun clearAllLiked()

    @Query("SELECT * FROM sayings WHERE title LIKE '%' || :title || '%'")
    fun searchSayingByTitle(title: String?): Flow<List<SayingEntity>>

    @Query("SELECT * FROM sayings WHERE title IN (:titles)")
    fun getSayingsByTitles(titles: List<String>): Flow<List<SayingEntity>>

    @Query("SELECT * FROM sayings")
    fun getAll(): Flow<List<SayingEntity>>
}