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
import com.swahilib.core.database.entities.content.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(search: SearchEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(searches: List<SearchEntity>)

    @Update
    fun update(search: SearchEntity)

    @Query("SELECT * FROM search WHERE id = :id")
    fun getById(id: String): Flow<SearchEntity>

    @Query("DELETE FROM search")
    fun delete()

    @Query("SELECT * FROM search WHERE title LIKE '%' || :title || '%'")
    fun searchSearchByTitle(title: String?): Flow<List<SearchEntity>>

    @Query("SELECT * FROM search")
    fun getAll(): Flow<List<SearchEntity>>
}