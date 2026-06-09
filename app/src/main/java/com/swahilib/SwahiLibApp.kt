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
        NotificationScheduler.scheduleNenoLaSiku(
            context = this,
            enabled = prefsRepo.nenoNotifEnabled,
            hour    = prefsRepo.nenoNotifHour,
            minute  = prefsRepo.nenoNotifMinute,
        )
        NotificationScheduler.scheduleMethaliYaSiku(
            context = this,
            enabled = prefsRepo.methaliNotifEnabled,
            hour    = prefsRepo.methaliNotifHour,
            minute  = prefsRepo.methaliNotifMinute,
        )
    }
}
