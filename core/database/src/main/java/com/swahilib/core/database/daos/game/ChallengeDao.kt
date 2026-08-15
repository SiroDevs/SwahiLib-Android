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

package com.swahilib.core.database.daos.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.swahilib.core.database.entities.game.ChallengeActivityEntity
import com.swahilib.core.database.entities.game.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ChallengeActivityEntity>)

    @Transaction
    suspend fun insertWithActivities(
        challenge: ChallengeEntity,
        activities: List<ChallengeActivityEntity>,
    ) {
        insertChallenge(challenge)
        insertActivities(activities)
    }

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateActivity(activity: ChallengeActivityEntity)

    @Query("SELECT * FROM challenges WHERE scope = :scope AND periodKey = :periodKey LIMIT 1")
    suspend fun getByPeriod(scope: String, periodKey: String): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ChallengeEntity?

    @Query("SELECT * FROM challenge_activities WHERE challengeId = :challengeId ORDER BY orderIndex ASC")
    suspend fun getActivitiesFor(challengeId: String): List<ChallengeActivityEntity>

    @Query("SELECT * FROM challenges WHERE expiresAt > :now ORDER BY expiresAt ASC")
    fun observeActive(now: Long): Flow<List<ChallengeEntity>>

    @Query("SELECT COUNT(*) FROM challenges WHERE completed = 1")
    suspend fun completedCount(): Int

    @Query("DELETE FROM challenges WHERE expiresAt < :cutoff AND completed = 0")
    suspend fun purgeExpired(cutoff: Long): Int

    @Query("DELETE FROM challenge_activities WHERE challengeId NOT IN (SELECT id FROM challenges)")
    suspend fun purgeOrphanActivities()

    @Query("DELETE FROM challenges")
    suspend fun deleteAllChallenges()

    @Query("DELETE FROM challenge_activities")
    suspend fun deleteAllActivities()
}
