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
import com.swahilib.core.database.entities.content.IdiomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdiomDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(idiomEntity: IdiomEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(idioms: List<IdiomEntity>)

    @Update
    fun update(idiomEntity: IdiomEntity)

    @Query("SELECT * FROM idioms WHERE rid = :rid")
    fun getById(rid: String): Flow<IdiomEntity>

    @Query("SELECT * FROM idioms WHERE rid = :rid LIMIT 1")
    suspend fun getByRid(rid: Int): IdiomEntity?

    @Query("DELETE FROM idioms")
    fun delete()

    @Query("UPDATE idioms SET liked = 0 WHERE liked = 1")
    suspend fun clearAllLiked()

    @Query("SELECT * FROM idioms WHERE title LIKE '%' || :title || '%'")
    fun searchIdiomByTitle(title: String?): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms WHERE title IN (:titles)")
    fun getIdiomsByTitles(titles: List<String>): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms")
    fun getAll(): Flow<List<IdiomEntity>>
}