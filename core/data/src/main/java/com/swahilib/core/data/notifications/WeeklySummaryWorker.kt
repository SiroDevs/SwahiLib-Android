package com.swahilib.core.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.swahilib.core.common.R
import com.swahilib.core.common.utils.DeepLinkConstants
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.games.EngagementRepo
import com.swahilib.core.data.repos.utils.PrefsRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val engageRepo: EngagementRepo,
    private val prefsRepo: PrefsRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val stats = engageRepo.statistics()
        val progress = engageRepo.currentProgress()

        if (stats.activeDaysThisWeek == 0 && progress.totalXp == 0L) return Result.success()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotifConstants.CHANNEL_SUMMARY_ID,
            NotifConstants.CHANNEL_SUMMARY_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Muhtasari wa juma lako la kujifunza" }
        manager.createNotificationChannel(channel)

        val weeklyXp = stats.weeklyActivity.sumOf { it.xpEarned }
        val minutes = stats.weeklyActivity.sumOf { it.secondsSpent } / 60
        val body = buildString {
            append("XP wiki hii: $weeklyXp • ")
            append("Siku hai: ${stats.activeDaysThisWeek}/7")
            if (minutes > 0) append(" • Dakika: $minutes")
        }
        val streak = prefsRepo.currentStreak
        val bigText = buildString {
            append(body)
            if (streak > 1) append("\n\n🔥 Mfuatano wako sasa ni siku $streak.")
            append("\nEndelea kukuza msamiati wako wa Kiswahili!")
        }

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, Routes.PROGRESS)
            }
        val pendingIntent = PendingIntent.getActivity(
            context, NotifConstants.NOTIF_SUMMARY_ID, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, NotifConstants.CHANNEL_SUMMARY_ID)
            .setSmallIcon(R.drawable.ic_swahilib_notification)
            .setContentTitle("📊 Muhtasari wa Wiki")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NotifConstants.NOTIF_SUMMARY_ID, notification)
        return Result.success()
    }
}
