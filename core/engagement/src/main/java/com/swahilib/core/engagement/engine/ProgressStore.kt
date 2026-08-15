package com.swahilib.core.engagement.engine

import com.swahilib.core.database.daos.game.AchievementRecordDao
import com.swahilib.core.database.daos.game.ChallengeDao
import com.swahilib.core.database.daos.daily.DailyActivityDao
import com.swahilib.core.database.daos.game.LearningHistoryDao
import com.swahilib.core.database.daos.game.UserProgressDao
import com.swahilib.core.database.daos.game.XpEventDao
import com.swahilib.core.database.entities.daily.DailyActivityEntity
import com.swahilib.core.database.entities.game.UserProgressEntity
import com.swahilib.core.engagement.time.Clock
import com.swahilib.core.engagement.time.SystemClock
import com.swahilib.core.engagement.time.TimeKeys

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
