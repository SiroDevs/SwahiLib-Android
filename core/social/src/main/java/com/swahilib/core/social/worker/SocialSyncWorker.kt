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

@HiltWorker
class SocialSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val engageRepo: EngagementRepo,
    private val authRepo: SocialAuthRepo,
    private val socialRepo: SocialRepo,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val signedIn = authRepo.isSignedIn.first()
        if (!signedIn) return Result.success()

        return runCatching {
            val progress = engageRepo.currentProgress()
            socialRepo.syncProgress(
                level = progress.level,
                totalXp = progress.totalXp.toInt(),
                currentStreak = progress.currentStreak,
            )

            val unlocked = engageRepo.achievementsWithStatus().filter { it.unlockedAt != null }
            unlocked.forEach { achievement -> socialRepo.postAchievementUnlock(achievement.id) }

            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        const val WORK_NAME = "social_sync_work"
    }
}
