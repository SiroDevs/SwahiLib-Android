package com.swahilib.core.data.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.swahilib.core.common.utils.NotifConstants
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleDailyWord(context: Context, enabled: Boolean, hour: Int, minute: Int) {
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
}
