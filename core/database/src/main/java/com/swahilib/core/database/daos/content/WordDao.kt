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
import com.swahilib.core.database.entities.content.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Update
    fun update(word: WordEntity)

    @Query("SELECT * FROM words WHERE rid = :rid")
    fun getById(rid: String): Flow<WordEntity>

    @Query("SELECT * FROM words WHERE rid = :rid LIMIT 1")
    suspend fun getByRid(rid: Int): WordEntity?

    @Query("DELETE FROM words")
    fun delete()

    @Query("UPDATE words SET liked = 0 WHERE liked = 1")
    suspend fun clearAllLiked()

    @Query("""
        SELECT * FROM words
        WHERE title LIKE '%' || :title || '%'
        ORDER BY
            CASE
                WHEN LOWER(title) = LOWER(:title)         THEN 0
                WHEN LOWER(title) LIKE LOWER(:title) || '%' THEN 1
                ELSE 2
            END,
            title ASC
    """)
    fun searchWordByTitle(title: String?): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE title IN (:titles)")
    fun getWordsByTitles(titles: List<String>): Flow<List<WordEntity>>

    @Query("SELECT * FROM words")
    fun getAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE title IS NOT NULL AND meaning IS NOT NULL ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): WordEntity?
}
