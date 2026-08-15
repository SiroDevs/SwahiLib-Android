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
import com.swahilib.core.database.entities.content.ProverbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProverbDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(proverb: ProverbEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(proverbs: List<ProverbEntity>)

    @Update
    fun update(proverb: ProverbEntity)

    @Query("SELECT * FROM proverbs WHERE rid = :rid")
    fun getById(rid: String): Flow<ProverbEntity>

    @Query("SELECT * FROM proverbs WHERE rid = :rid LIMIT 1")
    suspend fun getByRid(rid: Int): ProverbEntity?

    @Query("DELETE FROM proverbs")
    fun delete()

    @Query("UPDATE proverbs SET liked = 0 WHERE liked = 1")
    suspend fun clearAllLiked()

    @Query("SELECT * FROM proverbs WHERE title LIKE '%' || :title || '%'")
    fun searchProverbByTitle(title: String?): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE title IN (:titles)")
    fun getProverbsByTitles(titles: List<String>): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs")
    fun getAll(): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE title IS NOT NULL AND meaning IS NOT NULL ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomProverb(): ProverbEntity?
}
