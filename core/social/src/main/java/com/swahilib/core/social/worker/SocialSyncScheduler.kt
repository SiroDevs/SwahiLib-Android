package com.swahilib.core.social.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SocialSyncScheduler {

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<SocialSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(networkConstraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SocialSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SocialSyncWorker.WORK_NAME)
    }
}
