package com.swahilib

import android.app.Application
import androidx.work.Configuration
import com.swahilib.core.data.notifications.NotificationScheduler
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.data.worker.WorkManagerReadiness
import com.swahilib.widget.WidgetScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SwahiLibApp : Application() {

    @Inject lateinit var prefsRepo: PrefsRepo

    @Inject lateinit var workManagerConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()
        val workManagerReady = WorkManagerReadiness.tryInitialize(this, workManagerConfiguration)
        if (workManagerReady) {
            scheduleNotifications()
            WidgetScheduler.scheduleDailyRefresh(this)
        }
    }

    private fun scheduleNotifications() {
        NotificationScheduler.scheduleDailyWord(
            context = this,
            enabled = prefsRepo.wordNotifEnabled,
            hour = prefsRepo.wordNotifHour,
            minute = prefsRepo.wordNotifMinute,
        )
        NotificationScheduler.scheduleDailyProverb(
            context = this,
            enabled = prefsRepo.proverbNotifEnabled,
            hour = prefsRepo.proverbNotifHour,
            minute = prefsRepo.proverbNotifMinute,
        )
        NotificationScheduler.scheduleDailyChallenge(
            context = this,
            enabled = prefsRepo.challengeNotifEnabled,
            hour = prefsRepo.challengeNotifHour,
            minute = prefsRepo.challengeNotifMinute,
        )
        NotificationScheduler.scheduleWeeklySummary(
            context = this,
            enabled = prefsRepo.weeklySummaryNotifEnabled,
            hour = prefsRepo.weeklySummaryNotifHour,
            minute = prefsRepo.weeklySummaryNotifMinute,
        )
    }
}
