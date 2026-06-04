package com.swahilib.core.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {

    /** Constraints: needs any network connection. Battery / charging agnostic. */
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    /**
     * Schedules the first-install sync.
     * Uses [ExistingWorkPolicy.KEEP] so a second call (e.g. orientation change)
     * does not cancel an already-running sync.
     */
    fun scheduleInstallSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints)
            .addTag(SyncWorker.TAG)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SyncWorker.INSTALL_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Schedules the daily background re-sync.
     * Uses [ExistingWorkPolicy.KEEP] so if the user opens the app twice within
     * a short window the second call does nothing.
     */
    fun scheduleDailySync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints)
            .addTag(SyncWorker.TAG)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SyncWorker.DAILY_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
    }
}
