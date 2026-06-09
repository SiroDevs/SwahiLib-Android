package com.swahilib.core.data.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.swahilib.core.common.utils.NotifConstants
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    /**
     * Schedules (or re-schedules) the Neno la Siku daily notification.
     * Pass [enabled] = false to cancel it.
     */
    fun scheduleNenoLaSiku(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_WORD)
            return
        }
        val request = PeriodicWorkRequestBuilder<NenoLaSikuWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_WORD,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /**
     * Schedules (or re-schedules) the Methali ya Siku daily notification.
     * Pass [enabled] = false to cancel it.
     */
    fun scheduleMethaliYaSiku(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        val wm = WorkManager.getInstance(context)
        if (!enabled) {
            wm.cancelUniqueWork(NotifConstants.WORK_PROVERB)
            return
        }
        val request = PeriodicWorkRequestBuilder<MethaliYaSikuWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(
            NotifConstants.WORK_PROVERB,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /** Computes milliseconds until the next occurrence of [hour]:[minute]. */
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
