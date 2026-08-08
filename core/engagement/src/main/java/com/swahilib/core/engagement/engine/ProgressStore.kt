package com.swahilib.core.engagement.engine

import com.swahilib.core.database.daos.AchievementRecordDao
import com.swahilib.core.database.daos.ChallengeDao
import com.swahilib.core.database.daos.DailyActivityDao
import com.swahilib.core.database.daos.LearningHistoryDao
import com.swahilib.core.database.daos.UserProgressDao
import com.swahilib.core.database.daos.XpEventDao
import com.swahilib.core.database.model.DailyActivityEntity
import com.swahilib.core.database.model.UserProgressEntity
import com.swahilib.core.engagement.time.Clock
import com.swahilib.core.engagement.time.SystemClock
import com.swahilib.core.engagement.time.TimeKeys

/**
 * Thin facade over every DAO the engines need. Bundling them lets us swap in
 * an in-memory implementation for tests without every engine growing a
 * six-argument constructor. Also home to shared "read progress, mutate,
 * upsert" helpers so counters stay consistent no matter which engine called.
 */
class ProgressStore(
    val userProgressDao: UserProgressDao,
    val xpEventDao: XpEventDao,
    val achievementRecordDao: AchievementRecordDao,
    val challengeDao: ChallengeDao,
    val dailyActivityDao: DailyActivityDao,
    val learningHistoryDao: LearningHistoryDao,
    val clock: Clock = SystemClock,
) {

    suspend fun loadOrInitProgress(): UserProgressEntity {
        val existing = userProgressDao.get()
        if (existing != null) return existing
        val fresh = UserProgressEntity(updatedAt = clock.now())
        userProgressDao.upsert(fresh)
        return fresh
    }

    suspend fun writeProgress(entity: UserProgressEntity) {
        userProgressDao.upsert(entity.copy(updatedAt = clock.now()))
    }

    /**
     * Read-modify-write a single day's counters. Uses INSERT-OR-IGNORE +
     * UPDATE to stay safe against concurrent activity completions on the
     * same day (Room serializes on the DB writer anyway).
     */
    suspend fun mutateDay(
        date: String = TimeKeys.today(clock),
        block: (DailyActivityEntity) -> DailyActivityEntity,
    ): DailyActivityEntity {
        dailyActivityDao.insertIfMissing(DailyActivityEntity(date = date))
        val current = dailyActivityDao.getByDate(date) ?: DailyActivityEntity(date = date)
        val updated = block(current)
        if (updated != current) dailyActivityDao.update(updated)
        return updated
    }

    /** Wipes every engagement table (XP, progress, challenges, achievements, daily activity, learning history). */
    suspend fun clearAll() {
        xpEventDao.deleteAll()
        userProgressDao.deleteAll()
        challengeDao.deleteAllChallenges()
        challengeDao.deleteAllActivities()
        achievementRecordDao.deleteAll()
        dailyActivityDao.deleteAll()
        learningHistoryDao.deleteAll()
    }
}
