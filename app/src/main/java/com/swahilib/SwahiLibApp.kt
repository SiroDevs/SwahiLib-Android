package com.swahilib

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.swahilib.core.data.notifications.NotificationScheduler
import com.swahilib.core.data.repos.PrefsRepo
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SwahiLibApp : Application() {

    @Inject lateinit var prefsRepo: PrefsRepo

    /**
     * Injected by [WorkManagerModule] — includes the [HiltWorkerFactory]
     * so @HiltWorker classes resolve their dependencies.
     */
    @Inject lateinit var workManagerConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
        scheduleNotifications()
    }

    private fun scheduleNotifications() {
        NotificationScheduler.scheduleDailyWord(
            context = this,
            enabled = prefsRepo.wordNotifEnabled,
            hour    = prefsRepo.wordNotifHour,
            minute  = prefsRepo.wordNotifMinute,
        )
        NotificationScheduler.scheduleDailyProverb(
            context = this,
            enabled = prefsRepo.proverbNotifEnabled,
            hour    = prefsRepo.proverbNotifHour,
            minute  = prefsRepo.proverbNotifMinute,
        )
    }
}
