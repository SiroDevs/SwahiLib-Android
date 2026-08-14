package com.swahilib.core.data.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.data.worker.WorkManagerReadiness
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleDailyWord(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        if (!WorkManagerReadiness.isAvailable) return
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_WORD)
            return
        }
        val request = PeriodicWorkRequestBuilder<DailyWordWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_WORD,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleDailyProverb(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        if (!WorkManagerReadiness.isAvailable) return
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_PROVERB)
            return
        }
        val request = PeriodicWorkRequestBuilder<DailyProverbWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_PROVERB,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleDailyChallenge(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        if (!WorkManagerReadiness.isAvailable) return
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_CHALLENGE)
            return
        }
        val request = PeriodicWorkRequestBuilder<DailyChallengeWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_CHALLENGE,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleWeeklySummary(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        if (!WorkManagerReadiness.isAvailable) return
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_WEEKLY_SUMMARY)
            return
        }
        val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNextSunday(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_WEEKLY_SUMMARY,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun delayUntil(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    private fun delayUntilNextSunday(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            val daysUntilSunday = ((Calendar.SUNDAY - get(Calendar.DAY_OF_WEEK)) + 7) % 7
            add(Calendar.DAY_OF_YEAR, daysUntilSunday)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 7)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
