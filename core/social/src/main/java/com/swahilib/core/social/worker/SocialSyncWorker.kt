package com.swahilib.core.social.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.social.repos.SocialAuthRepo
import com.swahilib.core.social.repos.SocialRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Pushes the local XP/level/streak snapshot up to `profiles`, and re-posts
 * every currently-unlocked achievement to `achievements` (the table's
 * unique constraint on (user_id, achievement_id) makes re-posting an
 * unlocked-yesterday achievement a harmless no-op, not a duplicate). Only
 * does anything if the user is actually signed in - a no-op otherwise, so
 * scheduling this unconditionally is safe even for users who never opt
 * into community features.
 */
@HiltWorker
class SocialSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val engagementRepo: EngagementRepo,
    private val authRepo: SocialAuthRepo,
    private val socialRepo: SocialRepo,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val signedIn = authRepo.isSignedIn.first()
        if (!signedIn) return Result.success()

        return runCatching {
            val progress = engagementRepo.currentProgress()
            socialRepo.syncProgress(
                level = progress.level,
                totalXp = progress.totalXp.toInt(),
                currentStreak = progress.currentStreak,
            )

            val unlocked = engagementRepo.achievementsWithStatus().filter { it.unlockedAt != null }
            unlocked.forEach { achievement -> socialRepo.postAchievementUnlock(achievement.id) }

            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        const val WORK_NAME = "social_sync_work"
    }
}
