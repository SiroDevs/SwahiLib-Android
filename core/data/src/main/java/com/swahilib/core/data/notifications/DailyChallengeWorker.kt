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
class DailyChallengeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val engageRepo: EngagementRepo,
    private val prefsRepo: PrefsRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val challenge = engageRepo.activeChallenges()
            .firstOrNull { it.scope.name == "DAILY" }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotifConstants.CHANNEL_CHALLENGE_ID,
            NotifConstants.CHANNEL_CHALLENGE_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Kumbusho la Changamoto ya siku" }
        manager.createNotificationChannel(channel)

        if (challenge != null && challenge.completed) return Result.success()

        val remaining = challenge?.activities?.count { !it.completed } ?: 3
        val streak = prefsRepo.currentStreak
        val title = "🎯 Changamoto ya Leo Yakusubiri"
        val summary = when {
            remaining == 0 -> "Changamoto imekamilika. Anza tena kesho!"
            remaining == 1 -> "Umebakiwa na shughuli 1 tu. Kamilisha ndani ya dakika 2!"
            else -> "Umebakiwa na shughuli $remaining. Chukua dakika 5!"
        }
        val bigText = if (streak > 1) {
            "$summary\n\n🔥 Usivunje mfuatano wako wa siku $streak!"
        } else summary

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, Routes.CHALLENGES)
            }
        val pendingIntent = PendingIntent.getActivity(
            context, NotifConstants.NOTIF_CHALLENGE_ID, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, NotifConstants.CHANNEL_CHALLENGE_ID)
            .setSmallIcon(R.drawable.ic_swahilib_notification)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NotifConstants.NOTIF_CHALLENGE_ID, notification)
        return Result.success()
    }
}
